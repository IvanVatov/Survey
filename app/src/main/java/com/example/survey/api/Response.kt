package com.example.survey.api

import kotlinx.serialization.Serializable

@Serializable
class Response<T>(val result: T?, val error: String?, val isSuccess: Boolean)