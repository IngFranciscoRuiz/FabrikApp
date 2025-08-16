package com.fjrh.FabrikApp.data.remote

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.fjrh.FabrikApp.domain.result.Result
import com.fjrh.FabrikApp.domain.exception.SubscriptionException
import com.fjrh.FabrikApp.data.local.entity.IngredienteEntity
import com.fjrh.FabrikApp.data.local.entity.FormulaEntity
import com.fjrh.FabrikApp.data.local.entity.VentaEntity
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.data.local.entity.HistorialProduccionEntity
import com.fjrh.FabrikApp.data.local.entity.BalanceEntity
import com.fjrh.FabrikApp.data.local.entity.NotaEntity
import com.fjrh.FabrikApp.data.local.entity.PedidoProveedorEntity
import com.fjrh.FabrikApp.data.local.entity.UnidadMedidaEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseService @Inject constructor() {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    
    companion object {
        private const val COLLECTION_ACTIVATION_CODES = "activation_codes_premium_monthly"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_INGREDIENTES = "ingredientes"
        private const val COLLECTION_FORMULAS = "formulas"
        private const val COLLECTION_VENTAS = "ventas"
        private const val COLLECTION_PRODUCTOS_TERMINADOS = "productos_terminados"
        private const val COLLECTION_PEDIDOS = "pedidos"
        private const val COLLECTION_USER_DATA = "user_data"
        private const val COLLECTION_SHARED_DATA = "shared_data"
        private const val COLLECTION_INGREDIENTES_INVENTARIO = "ingredientes_inventario"
        private const val COLLECTION_HISTORIAL = "historial"
        private const val COLLECTION_BALANCE = "balance"
        private const val COLLECTION_NOTAS = "notas"
        private const val COLLECTION_PEDIDOS_PROVEEDOR = "pedidos_proveedor"
        private const val COLLECTION_UNIDADES_MEDIDA = "unidades_medida"
    }
    
    /**
     * Autenticación por email
     */
    suspend fun signInWithEmail(email: String, password: String): Result<String> {
        return try {
            println("FirebaseService: Intentando autenticar usuario: $email")
            
            // Verificar que Firebase esté inicializado
            try {
                FirebaseApp.getInstance()
                println("FirebaseService: Firebase está inicializado")
            } catch (e: Exception) {
                println("FirebaseService: ERROR: Firebase no está inicializado")
                return Result.Error(SubscriptionException("Firebase no está configurado correctamente"))
            }
            
            val result = auth.signInWithEmailAndPassword(email, password).await()
            println("FirebaseService: Resultado de autenticación: ${result.user?.uid}")
            
            result.user?.uid?.let { uid ->
                println("FirebaseService: Usuario autenticado exitosamente: $uid")
                
                // Crear documento de usuario si no existe
                try {
                    val userDoc = firestore.collection(COLLECTION_USER_DATA)
                        .document(uid)
                        .get()
                        .await()
                    
                    if (!userDoc.exists()) {
                        println("FirebaseService: Creando documento de usuario: $uid")
                        val userData = mapOf(
                            "email" to email,
                            "createdAt" to java.util.Date(),
                            "isActive" to true,
                            "lastLogin" to java.util.Date()
                        )
                        
                        firestore.collection(COLLECTION_USER_DATA)
                            .document(uid)
                            .set(userData)
                            .await()
                        
                        println("FirebaseService: ✅ Documento de usuario creado exitosamente")
                    } else {
                        println("FirebaseService: ✅ Documento de usuario ya existe")
                    }
                } catch (e: Exception) {
                    println("FirebaseService: ⚠️ Error al verificar/crear documento de usuario: ${e.message}")
                }
                
                Result.Success(uid)
            } ?: Result.Error(SubscriptionException("Error en autenticación: usuario nulo"))
        } catch (e: Exception) {
            println("FirebaseService: ERROR en autenticación: ${e.message}")
            println("FirebaseService: Tipo de error: ${e.javaClass.simpleName}")
            e.printStackTrace()
            Result.Error(SubscriptionException("Error de autenticación: ${e.message}"))
        }
    }
    
    /**
     * Registro de usuario
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<String> {
        return try {
            println("FirebaseService: Intentando registrar usuario: $email")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid?.let { uid ->
                println("FirebaseService: Usuario registrado exitosamente: $uid")
                
                // Crear documento de usuario en user_data
                val userData = mapOf(
                    "email" to email,
                    "createdAt" to java.util.Date(),
                    "isActive" to true,
                    "subscriptionType" to "trial",
                    "lastLogin" to java.util.Date()
                )
                
                firestore.collection(COLLECTION_USER_DATA)
                    .document(uid)
                    .set(userData)
                    .await()
                
                println("FirebaseService: ✅ Documento de usuario creado en user_data: $uid")
                
                // También crear en users (para compatibilidad)
                firestore.collection(COLLECTION_USERS)
                    .document(uid)
                    .set(userData)
                    .await()
                
                Result.Success(uid)
            } ?: Result.Error(SubscriptionException("Error en registro"))
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error en registro: ${e.message}")
            e.printStackTrace()
            Result.Error(SubscriptionException("Error de registro: ${e.message}"))
        }
    }
    
    /**
     * Sincronizar ingredientes (datos compartidos)
     */
    suspend fun syncIngredientes(ingredientes: List<IngredienteEntity>): Result<Unit> {
        return try {
            println("FirebaseService: Sincronizando ${ingredientes.size} ingredientes (datos compartidos)")
            
            val batch = firestore.batch()
            
            ingredientes.forEach { ingrediente ->
                val docRef = firestore.collection(COLLECTION_SHARED_DATA)
                    .document(COLLECTION_INGREDIENTES)
                    .collection("items")
                    .document(ingrediente.id.toString())
                
                val data = mapOf(
                    "id" to ingrediente.id,
                    "formulaId" to ingrediente.formulaId,
                    "nombre" to ingrediente.nombre,
                    "unidad" to ingrediente.unidad,
                    "cantidad" to ingrediente.cantidad,
                    "costoPorUnidad" to ingrediente.costoPorUnidad,
                    "lastModified" to java.util.Date(),
                    "createdBy" to (auth.currentUser?.email ?: "unknown")
                )
                
                println("FirebaseService: Guardando ingrediente: ${ingrediente.nombre} en ruta: shared_data/ingredientes/${ingrediente.id}")
                batch.set(docRef, data)
            }
            
            batch.commit().await()
            println("FirebaseService: ✅ Ingredientes sincronizados exitosamente")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al sincronizar ingredientes: ${e.message}")
            e.printStackTrace()
            Result.Error(SubscriptionException("Error al sincronizar ingredientes: ${e.message}"))
        }
    }
    
    /**
     * Sincronizar fórmulas (datos compartidos)
     */
    suspend fun syncFormulas(formulas: List<FormulaEntity>): Result<Unit> {
        return try {
            println("FirebaseService: Sincronizando ${formulas.size} fórmulas (datos compartidos)")
            
            val batch = firestore.batch()
            
            formulas.forEach { formula ->
                val docRef = firestore.collection(COLLECTION_SHARED_DATA)
                    .document(COLLECTION_FORMULAS)
                    .collection("items")
                    .document(formula.id.toString())
                
                val data = mapOf(
                    "id" to formula.id,
                    "nombre" to formula.nombre,
                    "lastModified" to java.util.Date(),
                    "createdBy" to (auth.currentUser?.email ?: "unknown")
                )
                
                println("FirebaseService: Guardando fórmula: ${formula.nombre} en ruta: shared_data/formulas/${formula.id}")
                batch.set(docRef, data)
            }
            
            batch.commit().await()
            println("FirebaseService: ✅ Fórmulas sincronizadas exitosamente")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al sincronizar fórmulas: ${e.message}")
            e.printStackTrace()
            Result.Error(SubscriptionException("Error al sincronizar fórmulas: ${e.message}"))
        }
    }
    
    /**
     * Sincronizar ventas (datos compartidos)
     */
    suspend fun syncVentas(ventas: List<VentaEntity>): Result<Unit> {
        return try {
            println("FirebaseService: Sincronizando ${ventas.size} ventas (datos compartidos)")
            
            val batch = firestore.batch()
            
            ventas.forEach { venta ->
                val docRef = firestore.collection(COLLECTION_SHARED_DATA)
                    .document(COLLECTION_VENTAS)
                    .collection("items")
                    .document(venta.id.toString())
                
                val data = mapOf(
                    "id" to venta.id,
                    "nombreProducto" to venta.nombreProducto,
                    "litrosVendidos" to venta.litrosVendidos,
                    "precioPorLitro" to venta.precioPorLitro,
                    "fecha" to venta.fecha,
                    "cliente" to venta.cliente,
                    "lastModified" to java.util.Date(),
                    "createdBy" to (auth.currentUser?.email ?: "unknown")
                )
                
                println("FirebaseService: Guardando venta: ${venta.nombreProducto} en ruta: shared_data/ventas/${venta.id}")
                batch.set(docRef, data)
            }
            
            batch.commit().await()
            println("FirebaseService: ✅ Ventas sincronizadas exitosamente")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al sincronizar ventas: ${e.message}")
            e.printStackTrace()
            Result.Error(SubscriptionException("Error al sincronizar ventas: ${e.message}"))
        }
    }
    
    /**
     * Descargar ingredientes (datos compartidos)
     */
    suspend fun downloadIngredientes(): Result<List<IngredienteEntity>> {
        return try {
            println("FirebaseService: Descargando ingredientes (datos compartidos)")
            
            val snapshot = firestore.collection(COLLECTION_SHARED_DATA)
                .document(COLLECTION_INGREDIENTES)
                .collection("items")
                .get()
                .await()
            
            val ingredientes = snapshot.documents.mapNotNull { doc ->
                IngredienteEntity(
                    id = doc.getLong("id") ?: 0,
                    formulaId = doc.getLong("formulaId") ?: 0,
                    nombre = doc.getString("nombre") ?: "",
                    unidad = doc.getString("unidad") ?: "",
                    cantidad = doc.getString("cantidad") ?: "",
                    costoPorUnidad = doc.getDouble("costoPorUnidad") ?: 0.0
                )
            }
            
            println("FirebaseService: ✅ Descargados ${ingredientes.size} ingredientes compartidos")
            Result.Success(ingredientes)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al descargar ingredientes: ${e.message}")
            Result.Error(SubscriptionException("Error al descargar ingredientes: ${e.message}"))
        }
    }
    
    /**
     * Descargar fórmulas (datos compartidos)
     */
    suspend fun downloadFormulas(): Result<List<FormulaEntity>> {
        return try {
            println("FirebaseService: Descargando fórmulas (datos compartidos)")
            
            val snapshot = firestore.collection(COLLECTION_SHARED_DATA)
                .document(COLLECTION_FORMULAS)
                .collection("items")
                .get()
                .await()
            
            val formulas = snapshot.documents.mapNotNull { doc ->
                FormulaEntity(
                    id = doc.getLong("id") ?: 0,
                    nombre = doc.getString("nombre") ?: ""
                )
            }
            
            println("FirebaseService: ✅ Descargadas ${formulas.size} fórmulas compartidas")
            Result.Success(formulas)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al descargar fórmulas: ${e.message}")
            Result.Error(SubscriptionException("Error al descargar fórmulas: ${e.message}"))
        }
    }
    
    /**
     * Descargar ventas (datos compartidos)
     */
    suspend fun downloadVentas(): Result<List<VentaEntity>> {
        return try {
            println("FirebaseService: Descargando ventas (datos compartidos)")
            
            val snapshot = firestore.collection(COLLECTION_SHARED_DATA)
                .document(COLLECTION_VENTAS)
                .collection("items")
                .get()
                .await()
            
            val ventas = snapshot.documents.mapNotNull { doc ->
                VentaEntity(
                    id = doc.getLong("id") ?: 0,
                    nombreProducto = doc.getString("nombreProducto") ?: "",
                    litrosVendidos = doc.getDouble("litrosVendidos")?.toFloat() ?: 0f,
                    precioPorLitro = doc.getDouble("precioPorLitro") ?: 0.0,
                    fecha = doc.getLong("fecha") ?: 0,
                    cliente = doc.getString("cliente")
                )
            }
            
            println("FirebaseService: ✅ Descargadas ${ventas.size} ventas compartidas")
            Result.Success(ventas)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al descargar ventas: ${e.message}")
            Result.Error(SubscriptionException("Error al descargar ventas: ${e.message}"))
        }
    }
    
    /**
     * Descargar ingredientes de inventario (datos compartidos)
     */
    suspend fun downloadIngredientesInventario(): Result<List<IngredienteInventarioEntity>> {
        return try {
            println("FirebaseService: Descargando ingredientes de inventario (datos compartidos)")
            
            val snapshot = firestore.collection(COLLECTION_SHARED_DATA)
                .document(COLLECTION_INGREDIENTES_INVENTARIO)
                .collection("items")
                .get()
                .await()
            
            val ingredientes = snapshot.documents.mapNotNull { doc ->
                IngredienteInventarioEntity(
                    id = doc.getLong("id") ?: 0,
                    nombre = doc.getString("nombre") ?: "",
                    cantidadDisponible = doc.getDouble("cantidadDisponible")?.toFloat() ?: 0f,
                    unidad = doc.getString("unidad") ?: "",
                    costoPorUnidad = doc.getDouble("costoPorUnidad") ?: 0.0,
                    proveedor = doc.getString("proveedor"),
                    fechaIngreso = doc.getLong("fechaIngreso") ?: System.currentTimeMillis()
                )
            }
            
            println("FirebaseService: ✅ Descargados ${ingredientes.size} ingredientes de inventario compartidos")
            Result.Success(ingredientes)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al descargar ingredientes de inventario: ${e.message}")
            Result.Error(SubscriptionException("Error al descargar ingredientes de inventario: ${e.message}"))
        }
    }
    
    /**
     * Descargar historial de producción (datos compartidos)
     */
    suspend fun downloadHistorial(): Result<List<HistorialProduccionEntity>> {
        return try {
            println("FirebaseService: Descargando historial de producción (datos compartidos)")
            
            val snapshot = firestore.collection(COLLECTION_SHARED_DATA)
                .document(COLLECTION_HISTORIAL)
                .collection("items")
                .get()
                .await()
            
            val historial = snapshot.documents.mapNotNull { doc ->
                HistorialProduccionEntity(
                    id = doc.getLong("id") ?: 0,
                    nombreFormula = doc.getString("nombreFormula") ?: "",
                    litrosProducidos = doc.getDouble("litrosProducidos")?.toFloat() ?: 0f,
                    fecha = doc.getLong("fecha") ?: 0
                )
            }
            
            println("FirebaseService: ✅ Descargado ${historial.size} historial de producción compartido")
            Result.Success(historial)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al descargar historial: ${e.message}")
            Result.Error(SubscriptionException("Error al descargar historial: ${e.message}"))
        }
    }
    
    /**
     * Descargar balance (datos compartidos)
     */
    suspend fun downloadBalance(): Result<List<BalanceEntity>> {
        return try {
            println("FirebaseService: Descargando balance (datos compartidos)")
            
            val snapshot = firestore.collection(COLLECTION_SHARED_DATA)
                .document(COLLECTION_BALANCE)
                .collection("items")
                .get()
                .await()
            
            val balance = snapshot.documents.mapNotNull { doc ->
                BalanceEntity(
                    id = doc.getLong("id") ?: 0,
                    concepto = doc.getString("concepto") ?: "",
                    monto = doc.getDouble("monto") ?: 0.0,
                    tipo = doc.getString("tipo") ?: "",
                    fecha = doc.getLong("fecha") ?: 0,
                    descripcion = doc.getString("descripcion")
                )
            }
            
            println("FirebaseService: ✅ Descargado ${balance.size} balance compartido")
            Result.Success(balance)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al descargar balance: ${e.message}")
            Result.Error(SubscriptionException("Error al descargar balance: ${e.message}"))
        }
    }
    
    /**
     * Descargar notas (datos compartidos)
     */
    suspend fun downloadNotas(): Result<List<NotaEntity>> {
        return try {
            println("FirebaseService: Descargando notas (datos compartidos)")
            
            val snapshot = firestore.collection(COLLECTION_SHARED_DATA)
                .document(COLLECTION_NOTAS)
                .collection("items")
                .get()
                .await()
            
            val notas = snapshot.documents.mapNotNull { doc ->
                NotaEntity(
                    id = doc.getLong("id") ?: 0,
                    titulo = doc.getString("titulo") ?: "",
                    contenido = doc.getString("contenido") ?: "",
                    fecha = doc.getLong("fecha") ?: 0
                )
            }
            
            println("FirebaseService: ✅ Descargadas ${notas.size} notas compartidas")
            Result.Success(notas)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al descargar notas: ${e.message}")
            Result.Error(SubscriptionException("Error al descargar notas: ${e.message}"))
        }
    }
    
    /**
     * Descargar pedidos a proveedor (datos compartidos)
     */
    suspend fun downloadPedidosProveedor(): Result<List<PedidoProveedorEntity>> {
        return try {
            println("FirebaseService: Descargando pedidos a proveedor (datos compartidos)")
            
            val snapshot = firestore.collection(COLLECTION_SHARED_DATA)
                .document(COLLECTION_PEDIDOS_PROVEEDOR)
                .collection("items")
                .get()
                .await()
            
            val pedidos = snapshot.documents.mapNotNull { doc ->
                PedidoProveedorEntity(
                    id = doc.getLong("id") ?: 0,
                    nombreProveedor = doc.getString("nombreProveedor") ?: "",
                    productos = doc.getString("productos") ?: "",
                    fecha = doc.getLong("fecha") ?: 0,
                    monto = doc.getDouble("monto") ?: 0.0,
                    estado = doc.getString("estado") ?: "",
                    descripcion = doc.getString("descripcion")
                )
            }
            
            println("FirebaseService: ✅ Descargados ${pedidos.size} pedidos a proveedor compartidos")
            Result.Success(pedidos)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al descargar pedidos a proveedor: ${e.message}")
            Result.Error(SubscriptionException("Error al descargar pedidos a proveedor: ${e.message}"))
        }
    }
    
    /**
     * Descargar unidades de medida (datos compartidos)
     */
    suspend fun downloadUnidadesMedida(): Result<List<UnidadMedidaEntity>> {
        return try {
            println("FirebaseService: Descargando unidades de medida (datos compartidos)")
            
            val snapshot = firestore.collection(COLLECTION_SHARED_DATA)
                .document(COLLECTION_UNIDADES_MEDIDA)
                .collection("items")
                .get()
                .await()
            
            val unidades = snapshot.documents.mapNotNull { doc ->
                UnidadMedidaEntity(
                    id = doc.getLong("id") ?: 0,
                    nombre = doc.getString("nombre") ?: "",
                    descripcion = doc.getString("descripcion"),
                    esActiva = doc.getBoolean("esActiva") ?: true
                )
            }
            
            println("FirebaseService: ✅ Descargadas ${unidades.size} unidades de medida compartidas")
            Result.Success(unidades)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al descargar unidades de medida: ${e.message}")
            Result.Error(SubscriptionException("Error al descargar unidades de medida: ${e.message}"))
        }
    }
    
    /**
     * Sincronización completa
     */
    suspend fun syncAllData(
        ingredientes: List<IngredienteEntity>,
        formulas: List<FormulaEntity>,
        ventas: List<VentaEntity>
    ): Result<Unit> {
        return try {
            syncIngredientes(ingredientes)
            syncFormulas(formulas)
            syncVentas(ventas)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(SubscriptionException("Error en sincronización completa: ${e.message}"))
        }
    }
    
    /**
     * Sincronizar ingredientes de inventario (datos compartidos)
     */
    suspend fun syncIngredientesInventario(ingredientes: List<IngredienteInventarioEntity>): Result<Unit> {
        return try {
            println("FirebaseService: Sincronizando ${ingredientes.size} ingredientes de inventario (datos compartidos)")
            
            val batch = firestore.batch()
            
            ingredientes.forEach { ingrediente ->
                val docRef = firestore.collection(COLLECTION_SHARED_DATA)
                    .document(COLLECTION_INGREDIENTES_INVENTARIO)
                    .collection("items")
                    .document(ingrediente.id.toString())
                
                val data = mapOf(
                    "id" to ingrediente.id,
                    "nombre" to ingrediente.nombre,
                    "cantidadDisponible" to ingrediente.cantidadDisponible,
                    "unidad" to ingrediente.unidad,
                    "costoPorUnidad" to ingrediente.costoPorUnidad,
                    "proveedor" to ingrediente.proveedor,
                    "fechaIngreso" to ingrediente.fechaIngreso,
                    "lastModified" to java.util.Date(),
                    "createdBy" to (auth.currentUser?.email ?: "unknown")
                )
                
                println("FirebaseService: Guardando ingrediente inventario: ${ingrediente.nombre} en ruta: shared_data/ingredientes_inventario/${ingrediente.id}")
                batch.set(docRef, data)
            }
            
            batch.commit().await()
            println("FirebaseService: ✅ Ingredientes de inventario sincronizados exitosamente")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al sincronizar ingredientes de inventario: ${e.message}")
            e.printStackTrace()
            Result.Error(SubscriptionException("Error al sincronizar ingredientes de inventario: ${e.message}"))
        }
    }
    
    /**
     * Sincronizar historial de producción (datos compartidos)
     */
    suspend fun syncHistorial(historial: List<HistorialProduccionEntity>): Result<Unit> {
        return try {
            println("FirebaseService: Sincronizando ${historial.size} registros de historial (datos compartidos)")
            
            val batch = firestore.batch()
            
            historial.forEach { registro ->
                val docRef = firestore.collection(COLLECTION_SHARED_DATA)
                    .document(COLLECTION_HISTORIAL)
                    .collection("items")
                    .document(registro.id.toString())
                
                val data = mapOf(
                    "id" to registro.id,
                    "nombreFormula" to registro.nombreFormula,
                    "litrosProducidos" to registro.litrosProducidos,
                    "fecha" to registro.fecha,
                    "lastModified" to java.util.Date(),
                    "createdBy" to (auth.currentUser?.email ?: "unknown")
                )
                
                println("FirebaseService: Guardando historial: ${registro.nombreFormula} en ruta: shared_data/historial/${registro.id}")
                batch.set(docRef, data)
            }
            
            batch.commit().await()
            println("FirebaseService: ✅ Historial sincronizado exitosamente")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al sincronizar historial: ${e.message}")
            e.printStackTrace()
            Result.Error(SubscriptionException("Error al sincronizar historial: ${e.message}"))
        }
    }
    
    /**
     * Sincronizar balance (datos compartidos)
     */
    suspend fun syncBalance(balance: List<BalanceEntity>): Result<Unit> {
        return try {
            println("FirebaseService: Sincronizando ${balance.size} registros de balance (datos compartidos)")
            
            val batch = firestore.batch()
            
            balance.forEach { registro ->
                val docRef = firestore.collection(COLLECTION_SHARED_DATA)
                    .document(COLLECTION_BALANCE)
                    .collection("items")
                    .document(registro.id.toString())
                
                val data = mapOf(
                    "id" to registro.id,
                    "concepto" to registro.concepto,
                    "monto" to registro.monto,
                    "tipo" to registro.tipo,
                    "fecha" to registro.fecha,
                    "descripcion" to registro.descripcion,
                    "lastModified" to java.util.Date(),
                    "createdBy" to (auth.currentUser?.email ?: "unknown")
                )
                
                println("FirebaseService: Guardando balance: ${registro.descripcion} en ruta: shared_data/balance/${registro.id}")
                batch.set(docRef, data)
            }
            
            batch.commit().await()
            println("FirebaseService: ✅ Balance sincronizado exitosamente")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al sincronizar balance: ${e.message}")
            e.printStackTrace()
            Result.Error(SubscriptionException("Error al sincronizar balance: ${e.message}"))
        }
    }
    
    /**
     * Sincronizar notas (datos compartidos)
     */
    suspend fun syncNotas(notas: List<NotaEntity>): Result<Unit> {
        return try {
            println("FirebaseService: Sincronizando ${notas.size} notas (datos compartidos)")
            
            val batch = firestore.batch()
            
            notas.forEach { nota ->
                val docRef = firestore.collection(COLLECTION_SHARED_DATA)
                    .document(COLLECTION_NOTAS)
                    .collection("items")
                    .document(nota.id.toString())
                
                val data = mapOf(
                    "id" to nota.id,
                    "titulo" to nota.titulo,
                    "contenido" to nota.contenido,
                    "fecha" to nota.fecha,
                    "lastModified" to java.util.Date(),
                    "createdBy" to (auth.currentUser?.email ?: "unknown")
                )
                
                println("FirebaseService: Guardando nota: ${nota.titulo} en ruta: shared_data/notas/${nota.id}")
                batch.set(docRef, data)
            }
            
            batch.commit().await()
            println("FirebaseService: ✅ Notas sincronizadas exitosamente")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al sincronizar notas: ${e.message}")
            e.printStackTrace()
            Result.Error(SubscriptionException("Error al sincronizar notas: ${e.message}"))
        }
    }
    
    /**
     * Sincronizar pedidos a proveedor (datos compartidos)
     */
    suspend fun syncPedidosProveedor(pedidos: List<PedidoProveedorEntity>): Result<Unit> {
        return try {
            println("FirebaseService: Sincronizando ${pedidos.size} pedidos a proveedor (datos compartidos)")
            
            val batch = firestore.batch()
            
            pedidos.forEach { pedido ->
                val docRef = firestore.collection(COLLECTION_SHARED_DATA)
                    .document(COLLECTION_PEDIDOS_PROVEEDOR)
                    .collection("items")
                    .document(pedido.id.toString())
                
                val data = mapOf(
                    "id" to pedido.id,
                    "nombreProveedor" to pedido.nombreProveedor,
                    "productos" to pedido.productos,
                    "fecha" to pedido.fecha,
                    "monto" to pedido.monto,
                    "estado" to pedido.estado,
                    "descripcion" to pedido.descripcion,
                    "lastModified" to java.util.Date(),
                    "createdBy" to (auth.currentUser?.email ?: "unknown")
                )
                
                println("FirebaseService: Guardando pedido: ${pedido.nombreProveedor} en ruta: shared_data/pedidos_proveedor/${pedido.id}")
                batch.set(docRef, data)
            }
            
            batch.commit().await()
            println("FirebaseService: ✅ Pedidos a proveedor sincronizados exitosamente")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al sincronizar pedidos a proveedor: ${e.message}")
            e.printStackTrace()
            Result.Error(SubscriptionException("Error al sincronizar pedidos a proveedor: ${e.message}"))
        }
    }
    
    /**
     * Sincronizar unidades de medida (datos compartidos)
     */
    suspend fun syncUnidadesMedida(unidades: List<UnidadMedidaEntity>): Result<Unit> {
        return try {
            println("FirebaseService: Sincronizando ${unidades.size} unidades de medida (datos compartidos)")
            
            val batch = firestore.batch()
            
            unidades.forEach { unidad ->
                val docRef = firestore.collection(COLLECTION_SHARED_DATA)
                    .document(COLLECTION_UNIDADES_MEDIDA)
                    .collection("items")
                    .document(unidad.id.toString())
                
                val data = mapOf(
                    "id" to unidad.id,
                    "nombre" to unidad.nombre,
                    "descripcion" to unidad.descripcion,
                    "esActiva" to unidad.esActiva,
                    "lastModified" to java.util.Date(),
                    "createdBy" to (auth.currentUser?.email ?: "unknown")
                )
                
                println("FirebaseService: Guardando unidad: ${unidad.nombre} en ruta: shared_data/unidades_medida/${unidad.id}")
                batch.set(docRef, data)
            }
            
            batch.commit().await()
            println("FirebaseService: ✅ Unidades de medida sincronizadas exitosamente")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error al sincronizar unidades de medida: ${e.message}")
            e.printStackTrace()
            Result.Error(SubscriptionException("Error al sincronizar unidades de medida: ${e.message}"))
        }
    }
    
    /**
     * Verifica un código de activación
     */
    suspend fun verifyActivationCode(code: String): Result<Boolean> {
        return try {
            val document = firestore.collection(COLLECTION_ACTIVATION_CODES)
                .document(code)
                .get()
                .await()
            
            if (document.exists()) {
                val isUsed = document.getBoolean("isUsed") ?: false
                val expiresAt = document.getTimestamp("expiresAt")
                
                if (isUsed) {
                    Result.Error(SubscriptionException("Código ya utilizado"))
                } else if (expiresAt != null && expiresAt.toDate().before(java.util.Date())) {
                    Result.Error(SubscriptionException("Código expirado"))
                } else {
                    // Marcar como usado
                    document.reference.update("isUsed", true).await()
                    Result.Success(true)
                }
            } else {
                Result.Error(SubscriptionException("Código inválido"))
            }
        } catch (e: Exception) {
            Result.Error(SubscriptionException("Error al verificar código: ${e.message}"))
        }
    }
    
    /**
     * Registra un usuario en Firestore
     */
    suspend fun registerUser(userId: String, email: String, subscriptionType: String): Result<Unit> {
        return try {
            val userData = mapOf(
                "email" to email,
                "subscriptionType" to subscriptionType,
                "createdAt" to java.util.Date(),
                "isActive" to true
            )
            
            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .set(userData)
                .await()
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(SubscriptionException("Error al registrar usuario: ${e.message}"))
        }
    }
    
    /**
     * Obtiene el usuario actual
     */
    fun getCurrentUser() = auth.currentUser
    
    /**
     * Verifica si hay un usuario autenticado
     */
    fun isUserAuthenticated(): Boolean = auth.currentUser != null
    
    /**
     * Cierra sesión
     */
    fun signOut() {
        auth.signOut()
    }
    
    /**
     * Migrar datos de user_data a shared_data
     */
    suspend fun migrateUserDataToShared(): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
            println("FirebaseService: Migrando datos de usuario $userId a datos compartidos")
            
            // Migrar ingredientes
            try {
                val ingredientesSnapshot = firestore.collection(COLLECTION_USER_DATA)
                    .document(userId)
                    .collection(COLLECTION_INGREDIENTES)
                    .get()
                    .await()
                
                if (!ingredientesSnapshot.isEmpty) {
                    println("FirebaseService: Migrando ${ingredientesSnapshot.size()} ingredientes")
                    val batch = firestore.batch()
                    
                    ingredientesSnapshot.documents.forEach { doc ->
                        val newDocRef = firestore.collection(COLLECTION_SHARED_DATA)
                            .document(COLLECTION_INGREDIENTES)
                            .collection("items")
                            .document(doc.id)
                        
                        batch.set(newDocRef, doc.data ?: emptyMap<String, Any>())
                    }
                    
                    batch.commit().await()
                    println("FirebaseService: ✅ Ingredientes migrados exitosamente")
                }
            } catch (e: Exception) {
                println("FirebaseService: ⚠️ Error migrando ingredientes: ${e.message}")
            }
            
            // Migrar fórmulas
            try {
                val formulasSnapshot = firestore.collection(COLLECTION_USER_DATA)
                    .document(userId)
                    .collection(COLLECTION_FORMULAS)
                    .get()
                    .await()
                
                if (!formulasSnapshot.isEmpty) {
                    println("FirebaseService: Migrando ${formulasSnapshot.size()} fórmulas")
                    val batch = firestore.batch()
                    
                    formulasSnapshot.documents.forEach { doc ->
                        val newDocRef = firestore.collection(COLLECTION_SHARED_DATA)
                            .document(COLLECTION_FORMULAS)
                            .collection("items")
                            .document(doc.id)
                        
                        batch.set(newDocRef, doc.data ?: emptyMap<String, Any>())
                    }
                    
                    batch.commit().await()
                    println("FirebaseService: ✅ Fórmulas migradas exitosamente")
                }
            } catch (e: Exception) {
                println("FirebaseService: ⚠️ Error migrando fórmulas: ${e.message}")
            }
            
            // Migrar ventas
            try {
                val ventasSnapshot = firestore.collection(COLLECTION_USER_DATA)
                    .document(userId)
                    .collection(COLLECTION_VENTAS)
                    .get()
                    .await()
                
                if (!ventasSnapshot.isEmpty) {
                    println("FirebaseService: Migrando ${ventasSnapshot.size()} ventas")
                    val batch = firestore.batch()
                    
                    ventasSnapshot.documents.forEach { doc ->
                        val newDocRef = firestore.collection(COLLECTION_SHARED_DATA)
                            .document(COLLECTION_VENTAS)
                            .collection("items")
                            .document(doc.id)
                        
                        batch.set(newDocRef, doc.data ?: emptyMap<String, Any>())
                    }
                    
                    batch.commit().await()
                    println("FirebaseService: ✅ Ventas migradas exitosamente")
                }
            } catch (e: Exception) {
                println("FirebaseService: ⚠️ Error migrando ventas: ${e.message}")
            }
            
            // Migrar ingredientes de inventario
            try {
                val ingredientesInventarioSnapshot = firestore.collection(COLLECTION_USER_DATA)
                    .document(userId)
                    .collection(COLLECTION_INGREDIENTES_INVENTARIO)
                    .get()
                    .await()
                
                if (!ingredientesInventarioSnapshot.isEmpty) {
                    println("FirebaseService: Migrando ${ingredientesInventarioSnapshot.size()} ingredientes de inventario")
                    val batch = firestore.batch()
                    
                    ingredientesInventarioSnapshot.documents.forEach { doc ->
                        val newDocRef = firestore.collection(COLLECTION_SHARED_DATA)
                            .document(COLLECTION_INGREDIENTES_INVENTARIO)
                            .collection("items")
                            .document(doc.id)
                        
                        batch.set(newDocRef, doc.data ?: emptyMap<String, Any>())
                    }
                    
                    batch.commit().await()
                    println("FirebaseService: ✅ Ingredientes de inventario migrados exitosamente")
                }
            } catch (e: Exception) {
                println("FirebaseService: ⚠️ Error migrando ingredientes de inventario: ${e.message}")
            }
            
            // Migrar historial
            try {
                val historialSnapshot = firestore.collection(COLLECTION_USER_DATA)
                    .document(userId)
                    .collection(COLLECTION_HISTORIAL)
                    .get()
                    .await()
                
                if (!historialSnapshot.isEmpty) {
                    println("FirebaseService: Migrando ${historialSnapshot.size()} registros de historial")
                    val batch = firestore.batch()
                    
                    historialSnapshot.documents.forEach { doc ->
                        val newDocRef = firestore.collection(COLLECTION_SHARED_DATA)
                            .document(COLLECTION_HISTORIAL)
                            .collection("items")
                            .document(doc.id)
                        
                        batch.set(newDocRef, doc.data ?: emptyMap<String, Any>())
                    }
                    
                    batch.commit().await()
                    println("FirebaseService: ✅ Historial migrado exitosamente")
                }
            } catch (e: Exception) {
                println("FirebaseService: ⚠️ Error migrando historial: ${e.message}")
            }
            
            // Migrar balance
            try {
                val balanceSnapshot = firestore.collection(COLLECTION_USER_DATA)
                    .document(userId)
                    .collection(COLLECTION_BALANCE)
                    .get()
                    .await()
                
                if (!balanceSnapshot.isEmpty) {
                    println("FirebaseService: Migrando ${balanceSnapshot.size()} registros de balance")
                    val batch = firestore.batch()
                    
                    balanceSnapshot.documents.forEach { doc ->
                        val newDocRef = firestore.collection(COLLECTION_SHARED_DATA)
                            .document(COLLECTION_BALANCE)
                            .collection("items")
                            .document(doc.id)
                        
                        batch.set(newDocRef, doc.data ?: emptyMap<String, Any>())
                    }
                    
                    batch.commit().await()
                    println("FirebaseService: ✅ Balance migrado exitosamente")
                }
            } catch (e: Exception) {
                println("FirebaseService: ⚠️ Error migrando balance: ${e.message}")
            }
            
            // Migrar notas
            try {
                val notasSnapshot = firestore.collection(COLLECTION_USER_DATA)
                    .document(userId)
                    .collection(COLLECTION_NOTAS)
                    .get()
                    .await()
                
                if (!notasSnapshot.isEmpty) {
                    println("FirebaseService: Migrando ${notasSnapshot.size()} notas")
                    val batch = firestore.batch()
                    
                    notasSnapshot.documents.forEach { doc ->
                        val newDocRef = firestore.collection(COLLECTION_SHARED_DATA)
                            .document(COLLECTION_NOTAS)
                            .collection("items")
                            .document(doc.id)
                        
                        batch.set(newDocRef, doc.data ?: emptyMap<String, Any>())
                    }
                    
                    batch.commit().await()
                    println("FirebaseService: ✅ Notas migradas exitosamente")
                }
            } catch (e: Exception) {
                println("FirebaseService: ⚠️ Error migrando notas: ${e.message}")
            }
            
            // Migrar pedidos a proveedor
            try {
                val pedidosSnapshot = firestore.collection(COLLECTION_USER_DATA)
                    .document(userId)
                    .collection(COLLECTION_PEDIDOS_PROVEEDOR)
                    .get()
                    .await()
                
                if (!pedidosSnapshot.isEmpty) {
                    println("FirebaseService: Migrando ${pedidosSnapshot.size()} pedidos a proveedor")
                    val batch = firestore.batch()
                    
                    pedidosSnapshot.documents.forEach { doc ->
                        val newDocRef = firestore.collection(COLLECTION_SHARED_DATA)
                            .document(COLLECTION_PEDIDOS_PROVEEDOR)
                            .collection("items")
                            .document(doc.id)
                        
                        batch.set(newDocRef, doc.data ?: emptyMap<String, Any>())
                    }
                    
                    batch.commit().await()
                    println("FirebaseService: ✅ Pedidos a proveedor migrados exitosamente")
                }
            } catch (e: Exception) {
                println("FirebaseService: ⚠️ Error migrando pedidos a proveedor: ${e.message}")
            }
            
            // Migrar unidades de medida
            try {
                val unidadesSnapshot = firestore.collection(COLLECTION_USER_DATA)
                    .document(userId)
                    .collection(COLLECTION_UNIDADES_MEDIDA)
                    .get()
                    .await()
                
                if (!unidadesSnapshot.isEmpty) {
                    println("FirebaseService: Migrando ${unidadesSnapshot.size()} unidades de medida")
                    val batch = firestore.batch()
                    
                    unidadesSnapshot.documents.forEach { doc ->
                        val newDocRef = firestore.collection(COLLECTION_SHARED_DATA)
                            .document(COLLECTION_UNIDADES_MEDIDA)
                            .collection("items")
                            .document(doc.id)
                        
                        batch.set(newDocRef, doc.data ?: emptyMap<String, Any>())
                    }
                    
                    batch.commit().await()
                    println("FirebaseService: ✅ Unidades de medida migradas exitosamente")
                }
            } catch (e: Exception) {
                println("FirebaseService: ⚠️ Error migrando unidades de medida: ${e.message}")
            }
            
            println("FirebaseService: ✅ Migración completada")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("FirebaseService: ❌ Error en migración: ${e.message}")
            Result.Error(SubscriptionException("Error en migración: ${e.message}"))
        }
    }
}
