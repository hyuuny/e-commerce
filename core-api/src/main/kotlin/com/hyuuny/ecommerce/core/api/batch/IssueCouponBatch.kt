package com.hyuuny.ecommerce.core.api.batch

import com.hyuuny.ecommerce.core.support.error.CouponNotFoundException
import com.hyuuny.ecommerce.storage.db.core.coupons.CouponRepository
import com.hyuuny.ecommerce.storage.db.core.coupons.UserCouponEntity
import com.hyuuny.ecommerce.storage.db.core.coupons.UserCouponRepository
import com.hyuuny.ecommerce.storage.db.core.users.UserEntity
import com.hyuuny.ecommerce.storage.db.core.users.UserRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.StepScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.data.RepositoryItemReader
import org.springframework.batch.item.data.RepositoryItemWriter
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import org.springframework.transaction.PlatformTransactionManager
import java.time.LocalDate

@Configuration
class IssueCouponBatch(
    private val jobRepository: JobRepository,
    private val platformTransactionManager: PlatformTransactionManager,
    private val userRepository: UserRepository,
    private val couponRepository: CouponRepository,
    private val userCouponRepository: UserCouponRepository,
) {
    @Bean
    fun issueCouponJob(): Job {
        return JobBuilder("issueCouponJob", jobRepository)
            .start(issueCouponStep())
            .build()
    }

    @Bean
    fun issueCouponStep(): Step {
        return StepBuilder("issueCouponStep", jobRepository)
            .chunk<UserEntity, UserCouponEntity>(10, platformTransactionManager)
            .reader(userBatchReader())
            .processor(issueCouponProcessor(null))
            .writer(userCouponWriter())
            .build()
    }

    @Bean
    fun userBatchReader(): RepositoryItemReader<UserEntity> {
        return RepositoryItemReaderBuilder<UserEntity>()
            .name("userBatchReader")
            .pageSize(10)
            .methodName("findAll")
            .repository(userRepository)
            .sorts(mapOf("id" to Sort.Direction.ASC))
            .build()
    }

    @StepScope
    @Bean
    fun issueCouponProcessor(@Value("#{jobParameters[code]}") code: String?): ItemProcessor<UserEntity, UserCouponEntity> {
        return ItemProcessor { user ->
            val coupon = code?.let { couponRepository.findByCode(it) }
                ?: throw CouponNotFoundException("쿠폰을 찾을 수 없습니다. code: $code")
            UserCouponEntity(
                userId = user.id,
                couponId = coupon.id,
                publishedDate = LocalDate.now(),
                expiredDate = coupon.expiredDate
            )
        }
    }

    @Bean
    fun userCouponWriter(): RepositoryItemWriter<UserCouponEntity> {
        return RepositoryItemWriterBuilder<UserCouponEntity>()
            .repository(userCouponRepository)
            .methodName("save")
            .build()
    }
}