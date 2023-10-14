package com.example.goldparfumadmin.admin.black_list.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldparfumadmin.repository.FireRepository
import com.example.goldparfumadmin.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlackListViewModel @Inject constructor(private val repository: FireRepository)  : ViewModel() {

    fun addUser(
        phoneNumber: String,
        context : Context
    ) = viewModelScope.launch {
        val state = repository.addUserToBlackList(phoneNumber)
        if (state.first)
            showToast(context, "Пользователь успешно добавлен!")
        else
            showToast(context, state.second)
    }

    fun deleteUser(
        phoneNumber : String,
        context : Context
    ) = viewModelScope.launch {
        val state = repository.deleteUserToBlackList(phoneNumber)
        if (state.first)
            showToast(context, "Пользователь успешно удалён!")
        else
            showToast(context, state.second)
    }

}