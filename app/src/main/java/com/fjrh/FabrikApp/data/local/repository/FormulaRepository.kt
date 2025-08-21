package com.fjrh.FabrikApp.data.local.repository

import com.fjrh.FabrikApp.data.local.AppDatabase
import com.fjrh.FabrikApp.data.local.dao.FormulaDao
import com.fjrh.FabrikApp.data.local.entity.FormulaConIngredientes
import com.fjrh.FabrikApp.data.local.entity.FormulaEntity
import com.fjrh.FabrikApp.data.local.entity.IngredienteEntity
import com.fjrh.FabrikApp.data.local.entity.IngredienteInventarioEntity
import com.fjrh.FabrikApp.data.local.entity.HistorialProduccionEntity
import com.fjrh.FabrikApp.data.local.entity.VentaEntity
import com.fjrh.FabrikApp.data.local.entity.BalanceEntity
import com.fjrh.FabrikApp.data.local.entity.UnidadMedidaEntity
import com.fjrh.FabrikApp.data.local.entity.NotaEntity
import com.fjrh.FabrikApp.data.local.entity.PedidoProveedorEntity
import com.fjrh.FabrikApp.domain.model.StockProducto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FormulaRepository @Inject constructor(
    private val formulaDao: FormulaDao,
    val database: AppDatabase
) {

    fun obtenerFormulasConIngredientes(): Flow<List<FormulaConIngredientes>> {
        return formulaDao.getAllFormulasConIngredientes()
    }

    suspend fun eliminarFormulaConIngredientes(formula: FormulaEntity) {
        formulaDao.deleteIngredientesByFormulaId(formula.id)
        formulaDao.deleteFormula(formula)
    }

    suspend fun eliminarIngredientesByFormulaId(formulaId: Long) {
        formulaDao.deleteIngredientesByFormulaId(formulaId)
    }

    suspend fun insertarFormulaConIngredientes(
        formula: FormulaEntity,
        ingredientes: List<IngredienteEntity>
    ) {
        formulaDao.insertarFormulaConIngredientes(formula, ingredientes)
    }

    suspend fun insertarHistorial(historial: HistorialProduccionEntity) {
        formulaDao.insertarHistorial(historial)
    }

    suspend fun insertarFormula(formula: FormulaEntity): Long {
        return formulaDao.insertFormula(formula)
    }

    suspend fun actualizarFormula(formula: FormulaEntity) {
        formulaDao.updateFormula(formula)
    }

    suspend fun insertarIngredientes(ingredientes: List<IngredienteEntity>) {
        formulaDao.insertIngredientes(ingredientes)
    }

    fun getStockProductos(): Flow<List<StockProducto>> {
        return formulaDao.getStockProductosConVentas().map { list ->
            list.map { StockProducto(nombre = it.nombreFormula, stock = it.stock) }
        }
    }

    suspend fun getStockProductosSync(): List<StockProducto> {
        return formulaDao.getStockProductosConVentasSync().map { 
            StockProducto(nombre = it.nombreFormula, stock = it.stock) 
        }
    }

    fun getIngredientesInventario(): Flow<List<IngredienteInventarioEntity>> {
        return formulaDao.getIngredientesInventario()
    }

    fun getHistorial(): Flow<List<HistorialProduccionEntity>> {
        return formulaDao.getHistorial()
    }

    suspend fun actualizarIngredienteInventario(ingrediente: IngredienteInventarioEntity) {
        formulaDao.actualizarIngredienteInventario(ingrediente)
    }

    suspend fun insertarIngredienteInventario(ingrediente: IngredienteInventarioEntity) {
        formulaDao.insertarIngredienteInventario(ingrediente)
    }

    // VENTAS
    fun getVentas(): Flow<List<VentaEntity>> {
        return formulaDao.getVentas()
    }

    suspend fun insertarVenta(venta: VentaEntity) {
        formulaDao.insertarVenta(venta)
    }

    suspend fun getLitrosVendidosPorProducto(nombreProducto: String): Float? {
        return formulaDao.getLitrosVendidosPorProducto(nombreProducto)
    }

    suspend fun eliminarVenta(venta: VentaEntity) {
        formulaDao.eliminarVenta(venta)
    }

    suspend fun eliminarVentaPorId(ventaId: Long) {
        formulaDao.eliminarVentaPorId(ventaId)
    }

    // BALANCE
    fun getBalance(): Flow<List<BalanceEntity>> {
        return formulaDao.getBalance()
    }

    suspend fun insertarBalance(balance: BalanceEntity) {
        formulaDao.insertarBalance(balance)
    }

    // UNIDADES DE MEDIDA
    fun getUnidadesMedida(): Flow<List<UnidadMedidaEntity>> {
        return formulaDao.getUnidadesMedida()
    }

    suspend fun insertarUnidadMedida(unidad: UnidadMedidaEntity) {
        formulaDao.insertarUnidadMedida(unidad)
    }

    suspend fun getUnidadesMedidaSync(): List<UnidadMedidaEntity> {
        return formulaDao.getUnidadesMedidaSync()
    }

    suspend fun actualizarUnidadMedida(unidad: UnidadMedidaEntity) {
        formulaDao.actualizarUnidadMedida(unidad)
    }

    suspend fun eliminarUnidadMedida(unidad: UnidadMedidaEntity) {
        formulaDao.eliminarUnidadMedida(unidad)
    }

    // NOTAS
    fun getNotas(): Flow<List<NotaEntity>> {
        return formulaDao.getNotas()
    }

    suspend fun insertarNota(nota: NotaEntity) {
        formulaDao.insertarNota(nota)
    }

    suspend fun actualizarNota(nota: NotaEntity) {
        formulaDao.actualizarNota(nota)
    }

    suspend fun eliminarNota(nota: NotaEntity) {
        formulaDao.eliminarNota(nota)
    }

    // PEDIDOS A PROVEEDOR
    fun getPedidosProveedor(): Flow<List<PedidoProveedorEntity>> {
        return formulaDao.getPedidosProveedor()
    }

    suspend fun insertarPedidoProveedor(pedido: PedidoProveedorEntity) {
        formulaDao.insertarPedidoProveedor(pedido)
    }

    suspend fun actualizarPedidoProveedor(pedido: PedidoProveedorEntity) {
        formulaDao.actualizarPedidoProveedor(pedido)
    }

    suspend fun eliminarPedidoProveedor(pedido: PedidoProveedorEntity) {
        formulaDao.eliminarPedidoProveedor(pedido)
    }

    suspend fun eliminarPedidoProveedorPorId(pedidoId: Long) {
        formulaDao.eliminarPedidoProveedorPorId(pedidoId)
    }

    // MÉTODOS PARA LIMPIAR DATOS (IMPORTACIÓN)
    suspend fun limpiarTodosLosDatos() {
        formulaDao.limpiarIngredientesInventario()
        formulaDao.limpiarFormulas()
        formulaDao.limpiarIngredientes()
        formulaDao.limpiarVentas()
        formulaDao.limpiarHistorial()
        formulaDao.limpiarBalance()
        formulaDao.limpiarNotas()
        formulaDao.limpiarPedidosProveedor()
    }

}
