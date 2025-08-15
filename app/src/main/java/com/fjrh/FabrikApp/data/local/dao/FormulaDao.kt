package com.fjrh.FabrikApp.data.local.dao

import androidx.room.*
import com.fjrh.FabrikApp.data.local.entity.*
import com.fjrh.FabrikApp.data.local.entity.NotaEntity
import com.fjrh.FabrikApp.data.local.entity.PedidoProveedorEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface FormulaDao {

    // 🧪 FORMULAS
    @Transaction
    @Query("SELECT * FROM formulas")
    fun getAllFormulasConIngredientes(): Flow<List<FormulaConIngredientes>>

    @Insert
    suspend fun insertFormula(formula: FormulaEntity): Long

    @Update
    suspend fun updateFormula(formula: FormulaEntity)

    @Insert
    suspend fun insertIngredientes(ingredientes: List<IngredienteEntity>)

    @Transaction
    suspend fun insertarFormulaConIngredientes(
        formula: FormulaEntity,
        ingredientes: List<IngredienteEntity>
    ) {
        val formulaId = insertFormula(formula)
        val ingredientesConId = ingredientes.map {
            it.copy(formulaId = formulaId)
        }
        insertIngredientes(ingredientesConId)
    }

    @Delete
    suspend fun deleteFormula(formula: FormulaEntity)

    @Query("DELETE FROM ingredientes WHERE formulaId = :formulaId")
    suspend fun deleteIngredientesByFormulaId(formulaId: Long)

    // 🕒 HISTORIAL DE PRODUCCIÓN
    @Insert
    suspend fun insertarHistorial(historial: HistorialProduccionEntity)

    @Query("SELECT * FROM historial_produccion ORDER BY fecha DESC")
    suspend fun obtenerHistorial(): List<HistorialProduccionEntity>

    @Query("SELECT * FROM historial_produccion ORDER BY fecha DESC")
    fun getHistorial(): Flow<List<HistorialProduccionEntity>>

    // 📦 INVENTARIO DE INGREDIENTES
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarIngredienteInventario(ingrediente: IngredienteInventarioEntity)

    @Update
    suspend fun actualizarIngredienteInventario(ingrediente: IngredienteInventarioEntity)

    @Delete
    suspend fun eliminarIngredienteInventario(ingrediente: IngredienteInventarioEntity)

    @Query("SELECT * FROM ingredientes_inventario ORDER BY nombre ASC")
    fun getIngredientesInventario(): Flow<List<IngredienteInventarioEntity>>

    // STOCK DE PRODUCTOS TERMINADOS
    @Query("SELECT nombreFormula, SUM(litrosProducidos) as stock FROM historial_produccion GROUP BY nombreFormula")
    fun getStockProductos(): Flow<List<StockProductoQuery>>

    @Query("SELECT nombreFormula, SUM(litrosProducidos) as stock FROM historial_produccion GROUP BY nombreFormula")
    suspend fun getStockProductosSync(): List<StockProductoQuery>

    @Query("SELECT nombreFormula, SUM(litrosProducidos) - COALESCE((SELECT SUM(litrosVendidos) FROM ventas WHERE nombreProducto = historial_produccion.nombreFormula), 0) as stock FROM historial_produccion GROUP BY nombreFormula")
    fun getStockProductosConVentas(): Flow<List<StockProductoQuery>>

    @Query("SELECT nombreFormula, SUM(litrosProducidos) - COALESCE((SELECT SUM(litrosVendidos) FROM ventas WHERE nombreProducto = historial_produccion.nombreFormula), 0) as stock FROM historial_produccion GROUP BY nombreFormula")
    suspend fun getStockProductosConVentasSync(): List<StockProductoQuery>

    // VENTAS
    @Insert
    suspend fun insertarVenta(venta: VentaEntity)

    @Query("SELECT * FROM ventas ORDER BY fecha DESC")
    fun getVentas(): Flow<List<VentaEntity>>

    @Query("SELECT SUM(litrosVendidos) FROM ventas WHERE nombreProducto = :nombreProducto")
    suspend fun getLitrosVendidosPorProducto(nombreProducto: String): Float?

    @Delete
    suspend fun eliminarVenta(venta: VentaEntity)

    @Query("DELETE FROM ventas WHERE id = :ventaId")
    suspend fun eliminarVentaPorId(ventaId: Long)

    // BALANCE
    @Insert
    suspend fun insertarBalance(balance: BalanceEntity)

    @Query("SELECT * FROM balance ORDER BY fecha DESC")
    fun getBalance(): Flow<List<BalanceEntity>>

    // UNIDADES DE MEDIDA
    @Insert
    suspend fun insertarUnidadMedida(unidad: UnidadMedidaEntity)

    @Query("SELECT * FROM unidades_medida WHERE esActiva = 1 ORDER BY nombre ASC")
    fun getUnidadesMedida(): Flow<List<UnidadMedidaEntity>>

    @Query("SELECT * FROM unidades_medida WHERE esActiva = 1 ORDER BY nombre ASC")
    suspend fun getUnidadesMedidaSync(): List<UnidadMedidaEntity>

    @Update
    suspend fun actualizarUnidadMedida(unidad: UnidadMedidaEntity)

    @Delete
    suspend fun eliminarUnidadMedida(unidad: UnidadMedidaEntity)

    // NOTAS
    @Insert
    suspend fun insertarNota(nota: NotaEntity)

    @Update
    suspend fun actualizarNota(nota: NotaEntity)

    @Delete
    suspend fun eliminarNota(nota: NotaEntity)

    @Query("SELECT * FROM notas ORDER BY fecha DESC")
    fun getNotas(): Flow<List<NotaEntity>>

    // PEDIDOS A PROVEEDOR
    @Insert
    suspend fun insertarPedidoProveedor(pedido: PedidoProveedorEntity)

    @Update
    suspend fun actualizarPedidoProveedor(pedido: PedidoProveedorEntity)

    @Delete
    suspend fun eliminarPedidoProveedor(pedido: PedidoProveedorEntity)

    @Query("DELETE FROM pedidos_proveedor WHERE id = :pedidoId")
    suspend fun eliminarPedidoProveedorPorId(pedidoId: Long)

    @Query("SELECT * FROM pedidos_proveedor ORDER BY fecha DESC")
    fun getPedidosProveedor(): Flow<List<PedidoProveedorEntity>>

    // MÉTODOS PARA LIMPIAR DATOS (IMPORTACIÓN)
    @Query("DELETE FROM ingredientes_inventario")
    suspend fun limpiarIngredientesInventario()

    @Query("DELETE FROM formulas")
    suspend fun limpiarFormulas()

    @Query("DELETE FROM ingredientes")
    suspend fun limpiarIngredientes()

    @Query("DELETE FROM ventas")
    suspend fun limpiarVentas()

    @Query("DELETE FROM historial_produccion")
    suspend fun limpiarHistorial()

    @Query("DELETE FROM balance")
    suspend fun limpiarBalance()

    @Query("DELETE FROM notas")
    suspend fun limpiarNotas()

    @Query("DELETE FROM pedidos_proveedor")
    suspend fun limpiarPedidosProveedor()

    // MÉTODOS PARA BACKUP - OBTENER TODOS LOS DATOS
    @Query("SELECT * FROM formulas")
    suspend fun getAllFormulas(): List<FormulaEntity>

    @Query("SELECT * FROM ingredientes")
    suspend fun getAllIngredientes(): List<IngredienteEntity>

    @Query("SELECT * FROM unidades_medida")
    suspend fun getAllUnidadesMedida(): List<UnidadMedidaEntity>

    @Query("SELECT * FROM ventas")
    suspend fun getAllVentas(): List<VentaEntity>

    @Query("SELECT * FROM balance")
    suspend fun getAllBalance(): List<BalanceEntity>

    @Query("SELECT * FROM notas")
    suspend fun getAllNotas(): List<NotaEntity>

    @Query("SELECT * FROM pedidos_proveedor")
    suspend fun getAllPedidosProveedor(): List<PedidoProveedorEntity>

    @Query("SELECT * FROM historial_produccion")
    suspend fun getAllHistorialProduccion(): List<HistorialProduccionEntity>

    @Query("SELECT * FROM ingredientes_inventario")
    suspend fun getAllIngredientesInventario(): List<IngredienteInventarioEntity>

    // MÉTODOS PARA BACKUP - INSERTAR DATOS
    @Insert
    suspend fun insertUnidad(unidad: UnidadMedidaEntity)

    @Insert
    suspend fun insertIngrediente(ingrediente: IngredienteEntity)

    @Insert
    suspend fun insertIngredienteInventario(ingredienteInventario: IngredienteInventarioEntity)

    @Insert
    suspend fun insertVenta(venta: VentaEntity)

    @Insert
    suspend fun insertBalance(balance: BalanceEntity)

    @Insert
    suspend fun insertNota(nota: NotaEntity)

    @Insert
    suspend fun insertPedidoProveedor(pedido: PedidoProveedorEntity)

    @Insert
    suspend fun insertHistorialProduccion(historial: HistorialProduccionEntity)

    // MÉTODOS PARA LIMPIAR DATOS ANTES DE IMPORTAR
    @Query("DELETE FROM historial_produccion")
    suspend fun limpiarHistorialProduccion()

    @Query("DELETE FROM unidades_medida")
    suspend fun limpiarUnidadesMedida()

}

// DTO para la consulta de stock
data class StockProductoQuery(
    val nombreFormula: String,
    val stock: Float
)
