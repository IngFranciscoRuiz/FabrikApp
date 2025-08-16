package com.fjrh.FabrikApp.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object WorkspaceService {
    private val auth get() = FirebaseAuth.getInstance()
    private val db get() = FirebaseFirestore.getInstance()

    suspend fun bootstrapWorkspaceForCurrentUser(): String {
        val uid = auth.currentUser?.uid ?: error("No auth")
        val email = auth.currentUser?.email
        val userRef = db.collection("users").document(uid)
        
        try {
            val snap = userRef.get().await()
            val wid = snap.getString("currentWorkspaceId")
            if (!wid.isNullOrBlank()) {
                println("WorkspaceService: Usuario ya tiene workspace: $wid")
                return wid
            }
        } catch (e: Exception) {
            println("WorkspaceService: Error al verificar workspace existente: ${e.message}")
        }

        // Crear nuevo workspace
        val wsRef = db.collection("workspaces").document()
        val workspaceData = mapOf(
            "name" to ("Workspace de " + (email ?: uid.take(6))),
            "ownerUid" to uid,
            "members" to listOf(uid),
            "roles" to mapOf(uid to "owner"),
            "createdAt" to FieldValue.serverTimestamp()
        )
        
        try {
            wsRef.set(workspaceData).await()
            println("WorkspaceService: Workspace creado: ${wsRef.id}")
            
            // Actualizar usuario con el workspace
            val userData = mapOf(
                "email" to email,
                "currentWorkspaceId" to wsRef.id,
                "joinedAt" to FieldValue.serverTimestamp()
            )
            
            userRef.set(userData, SetOptions.merge()).await()
            println("WorkspaceService: Usuario actualizado con workspace: ${wsRef.id}")
            
            return wsRef.id
        } catch (e: Exception) {
            println("WorkspaceService: Error al crear workspace: ${e.message}")
            throw e
        }
    }

    suspend fun joinWorkspace(wid: String) {
        val uid = auth.currentUser?.uid ?: error("No auth")
        val ref = db.collection("workspaces").document(wid)
        
        try {
            db.runTransaction { tx ->
                val doc = tx.get(ref)
                if (!doc.exists()) {
                    throw Exception("Workspace no existe")
                }
                
                val members = (doc.get("members") as? List<*>)?.map { "$it" } ?: emptyList()
                val roles = (doc.get("roles") as? Map<*, *>)?.mapKeys { "${it.key}" }?.mapValues { "${it.value}" } ?: emptyMap()
                
                tx.update(ref, mapOf(
                    "members" to (members + uid).distinct(),
                    "roles" to (roles + (uid to "editor"))
                ))
            }.await()
            
            // Actualizar usuario con el workspace
            db.collection("users").document(uid)
                .set(mapOf("currentWorkspaceId" to wid), SetOptions.merge()).await()
            
            WorkspaceHolder.set(wid)
            println("WorkspaceService: Usuario unido al workspace: $wid")
        } catch (e: Exception) {
            println("WorkspaceService: Error al unirse al workspace: ${e.message}")
            throw e
        }
    }

    suspend fun getCurrentWorkspaceInfo(): Map<String, Any>? {
        val uid = auth.currentUser?.uid ?: return null
        val userRef = db.collection("users").document(uid)
        
        try {
            val userDoc = userRef.get().await()
            val wid = userDoc.getString("currentWorkspaceId") ?: return null
            
            val workspaceRef = db.collection("workspaces").document(wid)
            val workspaceDoc = workspaceRef.get().await()
            
            if (workspaceDoc.exists()) {
                return workspaceDoc.data
            }
        } catch (e: Exception) {
            println("WorkspaceService: Error al obtener info del workspace: ${e.message}")
        }
        
        return null
    }

    suspend fun getCurrentWidOrNull(): String? {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        val snap = Firebase.firestore.collection("users").document(uid).get().await()
        return snap.getString("currentWorkspaceId")
    }

    suspend fun createWorkspaceForCurrentUser(name: String? = null): String {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: error("No auth")
        val email = auth.currentUser?.email
        val wsRef = Firebase.firestore.collection("workspaces").document()
        val wsData = mapOf(
            "name" to (name ?: "Workspace de " + (email ?: uid.take(6))),
            "ownerUid" to uid,
            "members" to listOf(uid),
            "roles" to mapOf(uid to "owner"),
            "createdAt" to FieldValue.serverTimestamp()
        )
        wsRef.set(wsData).await()
        Firebase.firestore.collection("users").document(uid)
            .set(mapOf("email" to email, "currentWorkspaceId" to wsRef.id,
                       "joinedAt" to FieldValue.serverTimestamp()), SetOptions.merge()).await()
        return wsRef.id
    }
}
