package com.example.ballapp.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val mainActivityRepository: MainActivityRepository) :
    ViewModel() {
    val updateUsers = MutableLiveData<UpdateUsers>()
    val updateTeams = MutableLiveData<UpdateTeams>()

    sealed class UpdateUsers {
        // Khi cập nhật thành công , hàm trả về dối tượng ResultOk.. và ngược lại, khi cập nhật ko
        // thành công ( xảy ra lỗi ) ra trong quá trình cập nhật hàm trả về dối tượng ResultError
        object ResultOk : UpdateUsers()
        object ResultError : UpdateUsers()
    }

    sealed class UpdateTeams {
        object ResultOk : UpdateTeams()
        object ResultError : UpdateTeams()
    }

    fun updateUser(
        userUID: String,
        avatarUrl: String,
    ) {
        // viewModelScope đc sử dụng để khỏi chạy và qly các coroutine
        // khi viewModel bị hủy, tất cả cách conroutine bị hủy theo
        // bắt đầu 1 coroutine để thực hiện việc cập nhật thông tin người dùng.
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            mainActivityRepository.updateUser(userUID, avatarUrl, {
                updateUsers.value = UpdateUsers.ResultOk
            }, {
                updateUsers.value = UpdateUsers.ResultError
            })
        }
    }

    fun updateTeams(
        userUID: String,
        teamImageUrl: String,
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            mainActivityRepository.updateTeam(userUID, teamImageUrl, {
                updateTeams.value = UpdateTeams.ResultOk
            }, {
                updateTeams.value = UpdateTeams.ResultError
            })

        }

    }
}