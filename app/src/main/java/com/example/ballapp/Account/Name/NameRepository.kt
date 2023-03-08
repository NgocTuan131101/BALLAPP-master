package com.example.ballapp.Account.Name

import com.example.ballapp.Model.UsersModel
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class NameRepository @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {
    fun saveUser(
        userUid: String,
        userName: String,
        userPhone: String,
        // hàm lambda dùng để lưu thông tin người dùng thàng công vs hàm số là 1
        //chuỗi và thông báo thành công
        onSuccess: (String) -> Unit,
        // ngược lại hàm onSuccess
        onFail: (String) -> Unit,
    ) {
        val user = UsersModel(userUid, userName, userPhone)
        firebaseDatabase.getReference("Users").child(userUid).setValue(user)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess(it.toString())
                } else {
                    onFail(it.exception?.message.orEmpty())
                }
            }
            .addOnFailureListener {
                onFail(it.message.orEmpty())
            }

    }
}