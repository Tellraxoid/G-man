package com.stem.stemtraining
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CancellationException
import org.junit.Assert.*
import org.junit.Test

class HealthPermissionTest {
    @Test fun deniedPermissionNeverReads()=runBlocking {
        var called=false
        val result=permittedHealthMetric("Вес","weight",emptySet()){called=true;error("must not read")}
        assertFalse(called);assertEquals("Доступ не разрешён",result.value);assertEquals("",result.detail)
    }
    @Test fun allowedEmptyDataStaysMissing()=runBlocking {
        assertEquals("Нет данных",permittedHealthMetric("Вес","weight",setOf("weight")){HealthMetric("Вес","Нет данных")}.value)
    }
    @Test fun revocationErasesValueAndSource(){
        val result=maskRevokedHealth(listOf(HealthMetric("Вес","80 кг","private-source")),listOf("weight"),emptySet()).single()
        assertEquals("Доступ не разрешён",result.value);assertTrue(result.detail.isEmpty())
    }
    @Test fun partialGrantsPreserveOnlyAllowed(){
        val results=maskRevokedHealth(listOf(HealthMetric("Вес","80 кг"),HealthMetric("Сон","8 ч")),listOf("weight","sleep"),setOf("sleep"))
        assertEquals("Доступ не разрешён",results[0].value);assertEquals("8 ч",results[1].value)
    }
    @Test fun securityErrorsAreNotZeros()=runBlocking {
        assertEquals("Разрешение отозвано",permittedHealthMetric("Вес","weight",setOf("weight")){throw SecurityException("sensitive")}.value)
    }
    @Test fun cancellationIsNotSwallowed()=runBlocking {
        try{permittedHealthMetric("Вес","weight",setOf("weight")){throw CancellationException()};fail("Expected cancellation")}catch(expected:CancellationException){ }
    }
    @Test fun unexpectedErrorsDoNotExposeDetails()=runBlocking {
        val result=permittedHealthMetric("Вес","weight",setOf("weight")){error("private-record")}
        assertFalse(result.toString().contains("private-record"))
    }
}
