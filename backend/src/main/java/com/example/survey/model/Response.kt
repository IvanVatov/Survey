package com.example.survey.model

import kotlinx.serialization.Serializable

@Serializable
class Response<T>(val result: T?, val error: String?, val isSuccess: Boolean)