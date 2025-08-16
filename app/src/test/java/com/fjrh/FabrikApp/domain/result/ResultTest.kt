package com.fjrh.FabrikApp.domain.result

import com.fjrh.FabrikApp.domain.exception.ValidationException
import org.junit.Assert.*
import org.junit.Test

class ResultTest {

    @Test
    fun `Success should have correct properties`() {
        // Given
        val data = "test data"
        val result = Result.Success(data)

        // Then
        assertTrue(result.isSuccess())
        assertFalse(result.isError())
        assertFalse(result.isLoading())
        assertEquals(data, result.getOrNull())
        assertEquals(data, result.getOrThrow())
    }

    @Test
    fun `Error should have correct properties`() {
        // Given
        val exception = ValidationException("Test error")
        val result = Result.Error(exception)

        // Then
        assertFalse(result.isSuccess())
        assertTrue(result.isError())
        assertFalse(result.isLoading())
        assertNull(result.getOrNull())
    }

    @Test
    fun `Loading should have correct properties`() {
        // Given
        val result = Result.Loading

        // Then
        assertFalse(result.isSuccess())
        assertFalse(result.isError())
        assertTrue(result.isLoading())
        assertNull(result.getOrNull())
    }

    @Test(expected = ValidationException::class)
    fun `getOrThrow should throw exception for Error`() {
        // Given
        val exception = ValidationException("Test error")
        val result = Result.Error(exception)

        // When
        result.getOrThrow()
    }

    @Test(expected = IllegalStateException::class)
    fun `getOrThrow should throw exception for Loading`() {
        // Given
        val result = Result.Loading

        // When
        result.getOrThrow()
    }

    @Test
    fun `onSuccess should execute action for Success`() {
        // Given
        val data = "test data"
        val result = Result.Success(data)
        var executed = false
        var capturedData = ""

        // When
        result.onSuccess { 
            executed = true
            capturedData = it
        }

        // Then
        assertTrue(executed)
        assertEquals(data, capturedData)
    }

    @Test
    fun `onSuccess should not execute action for Error`() {
        // Given
        val exception = ValidationException("Test error")
        val result = Result.Error(exception)
        var executed = false

        // When
        result.onSuccess { 
            executed = true
        }

        // Then
        assertFalse(executed)
    }

    @Test
    fun `onError should execute action for Error`() {
        // Given
        val exception = ValidationException("Test error")
        val result = Result.Error(exception)
        var executed = false
        var capturedException: ValidationException? = null

        // When
        result.onError { 
            executed = true
            capturedException = it as ValidationException
        }

        // Then
        assertTrue(executed)
        assertEquals(exception, capturedException)
    }

    @Test
    fun `onError should not execute action for Success`() {
        // Given
        val data = "test data"
        val result = Result.Success(data)
        var executed = false

        // When
        result.onError { 
            executed = true
        }

        // Then
        assertFalse(executed)
    }

    @Test
    fun `onLoading should execute action for Loading`() {
        // Given
        val result = Result.Loading
        var executed = false

        // When
        result.onLoading { 
            executed = true
        }

        // Then
        assertTrue(executed)
    }

    @Test
    fun `onLoading should not execute action for Success`() {
        // Given
        val data = "test data"
        val result = Result.Success(data)
        var executed = false

        // When
        result.onLoading { 
            executed = true
        }

        // Then
        assertFalse(executed)
    }

    @Test
    fun `map should transform Success data`() {
        // Given
        val data = "test"
        val result = Result.Success(data)

        // When
        val mapped = result.map { it.length }

        // Then
        assertTrue(mapped is Result.Success)
        assertEquals(4, (mapped as Result.Success).data)
    }

    @Test
    fun `map should preserve Error`() {
        // Given
        val exception = ValidationException("Test error")
        val result = Result.Error(exception)

        // When
        val mapped = result.map { it.length }

        // Then
        assertTrue(mapped is Result.Error)
        assertEquals(exception, (mapped as Result.Error).exception)
    }

    @Test
    fun `map should preserve Loading`() {
        // Given
        val result = Result.Loading

        // When
        val mapped = result.map { it.length }

        // Then
        assertTrue(mapped is Result.Loading)
    }

    @Test
    fun `mapError should transform Error`() {
        // Given
        val exception = ValidationException("Test error")
        val result = Result.Error(exception)

        // When
        val mapped = result.mapError { ValidationException("Transformed: ${it.message}") }

        // Then
        assertTrue(mapped is Result.Error)
        assertEquals("Transformed: Test error", (mapped as Result.Error).exception.message)
    }

    @Test
    fun `mapError should preserve Success`() {
        // Given
        val data = "test data"
        val result = Result.Success(data)

        // When
        val mapped = result.mapError { ValidationException("Should not happen") }

        // Then
        assertTrue(mapped is Result.Success)
        assertEquals(data, (mapped as Result.Success).data)
    }

    @Test
    fun `flatMap should chain Success operations`() {
        // Given
        val data = "test"
        val result = Result.Success(data)

        // When
        val flatMapped = result.flatMap { 
            if (it.length > 3) {
                Result.Success(it.uppercase())
            } else {
                Result.Error(ValidationException("Too short"))
            }
        }

        // Then
        assertTrue(flatMapped is Result.Success)
        assertEquals("TEST", (flatMapped as Result.Success).data)
    }

    @Test
    fun `flatMap should preserve Error`() {
        // Given
        val exception = ValidationException("Test error")
        val result = Result.Error(exception)

        // When
        val flatMapped = result.flatMap { 
            Result.Success("Should not happen")
        }

        // Then
        assertTrue(flatMapped is Result.Error)
        assertEquals(exception, (flatMapped as Result.Error).exception)
    }
}

