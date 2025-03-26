package com.hyuuny.ecommerce.core.api.v1.coupons

import com.hyuuny.ecommerce.core.support.response.ApiResponse
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/v1/batch/coupons")
@RestController
class CouponBatchController(
    private val jobLauncher: JobLauncher,
    private val jobRegistry: JobRegistry,
) {
    @GetMapping("/code/{code}/issue")
    fun allIssueCoupon(@PathVariable code: String): ApiResponse<Any> {
        val jobParameters = JobParametersBuilder()
            .addString("code", code)
            .toJobParameters()
        jobLauncher.run(jobRegistry.getJob("issueCouponJob"), jobParameters)
        return ApiResponse.success()
    }
}