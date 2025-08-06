package com.fjrh.laxcleanfactory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos_proveedor")
data class PedidoProveedorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombreProveedor: String,
    val productos: String, // JSON string con lista de productos
    val fecha: Long = System.currentTimeMillis(),
    val monto: Double,
    val estado: String = "PENDIENTE", // PENDIENTE, PAGADO
    val descripcion: String? = null
) 
