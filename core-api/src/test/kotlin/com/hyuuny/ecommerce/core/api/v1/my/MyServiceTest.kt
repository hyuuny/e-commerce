package com.hyuuny.ecommerce.core.api.v1.my

import com.hyuuny.ecommerce.core.api.v1.likes.LikeCommand
import com.hyuuny.ecommerce.core.api.v1.likes.LikeReader
import com.hyuuny.ecommerce.core.api.v1.likes.LikeService
import com.hyuuny.ecommerce.core.api.v1.likes.LikeWriter
import com.hyuuny.ecommerce.storage.db.core.likes.LikeEntity
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LikeServiceTest {
    private lateinit var writer: LikeWriter
    private lateinit var reader: LikeReader
    private lateinit var service: LikeService

    @BeforeEach
    fun setUp() {
        writer = mockk()
        reader = mockk()
        service = LikeService(reader, writer)
    }

    @Test
    fun `사용자가 좋아요한 상품이 아니면, 좋아요 처리된다`() {
        val command = LikeCommand(
            userId = 1L,
            productId = 1L
        )
        every { reader.isLiked(any(), any()) } returns null
        every { writer.like(any()) } returns LikeEntity(userId = command.userId, productId = command.productId)

        service.toggleLike(command)

        verify(exactly = 1) { writer.like(any()) }
        verify(exactly = 0) { writer.unlike(any()) }
    }

    @Test
    fun `사용자가 좋아요한 상품이면, 좋아요가 해제된다`() {
        val likeEntity = LikeEntity(
            userId = 1L,
            productId = 1L
        )
        val command = LikeCommand(
            userId = likeEntity.userId,
            productId = likeEntity.productId,
        )
        every { reader.isLiked(any(), any()) } returns likeEntity
        every { writer.unlike(any()) } just Runs

        service.toggleLike(command)

        verify(exactly = 0) { writer.like(any()) }
        verify(exactly = 1) { writer.unlike(any()) }
    }
}