package com.fjrh.FabrikApp.data.firebase

object WorkspaceHolder {
    @Volatile 
    private var wid: String? = null
    
    fun set(id: String) { 
        wid = id 
        println("WorkspaceHolder: Workspace ID establecido: $id")
    }
    
    fun get(): String = wid ?: error("Workspace ID no inicializado")
    
    fun isInitialized(): Boolean = wid != null
    
    fun clear() {
        wid = null
        println("WorkspaceHolder: Workspace ID limpiado")
    }
}
