package com.example.ballapp.Account.Name

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NameViewModel @Inject constructor(private val NameRepository: NameRepository) : ViewModel() {
    val saveUsers = MutableLiveData<SaveUsers>()

    sealed class SaveUsers {
        object ResultOk : SaveUsers()
        class ResultError(val errorMessage: String) : SaveUsers()

    }

    fun saveUsers(
        userUid: String,
        userName: String,
        userPhone: String,
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            NameRepository.saveUser(userUid, userName, userPhone, {
                saveUsers.value = SaveUsers.ResultOk
            }, {
                saveUsers.value = SaveUsers.ResultError(it)
            })
        }

    }
}