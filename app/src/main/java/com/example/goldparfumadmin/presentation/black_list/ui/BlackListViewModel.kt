package com.example.goldparfumadmin.presentation.black_list.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.data.repository.FireRepository
import com.example.goldparfumadmin.data.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlackListViewModel @Inject constructor(private val repository: FireRepository)  : ViewModel() {

    fun addUser(
        phoneNumber: String,
        context : Context
    ) = viewModelScope.launch {
        val result = repository.addUserToBlackList(phoneNumber)
        if (result.isSuccess)
            showToast(context, "Пользователь успешно добавлен!")
        else
            showToast(context, "${result.exceptionOrNull()} ${result.exceptionOrNull()?.message}")
    }

    fun deleteUser(
        phoneNumber : String,
        context : Context
    ) = viewModelScope.launch {
        val result = repository.deleteUserFromBlackList(phoneNumber)
        if (result.isSuccess)
            showToast(context, "Пользователь успешно удалён!")
        else
            showToast(context, "${result.exceptionOrNull()} ${result.exceptionOrNull()?.message}")
    }

}