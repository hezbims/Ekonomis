package com.hezapp.ekonomis.app_test_utils.product_preview.application.mock

import com.hezapp.ekonomis.core.domain.general_model.MyBasicError
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.product.model.PreviewProductSummary
import com.hezapp.ekonomis.product_preview.application.use_case.iface.IGetPreviewProductSummariesUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive

/**
 * Fake use case yang bisa menahan response query tertentu agar urutan response
 * bisa dibuat tidak berurutan di dalam test.
 */
class FakeGetPreviewProductSummariesUseCase : IGetPreviewProductSummariesUseCase {

    val requestedQueries = mutableListOf<String>()
    val cancelledQueries = mutableListOf<String>()

    private val outcomes = mutableMapOf<String, ArrayDeque<Outcome>>()

    fun returns(
        query: String,
        products: List<PreviewProductSummary>,
    ) {
        enqueue(query, Outcome.Success(products))
    }

    fun fails(query: String) {
        enqueue(query, Outcome.Failure)
    }

    /**
     * Response untuk [query] baru akan dikirim setelah [gate] di-complete.
     */
    fun gate(query: String): CompletableDeferred<List<PreviewProductSummary>> {
        val gate = CompletableDeferred<List<PreviewProductSummary>>()
        enqueue(query, Outcome.Gated(gate))
        return gate
    }

    private fun enqueue(query: String, outcome: Outcome) {
        outcomes.getOrPut(query) { ArrayDeque() }.addLast(outcome)
    }

    override fun invoke(
        searchQuery: String,
    ): Flow<ResponseWrapper<List<PreviewProductSummary>, MyBasicError>> = flow {
        requestedQueries += searchQuery
        emit(ResponseWrapper.Loading())

        try {
            when (val outcome = outcomes[searchQuery]?.removeFirstOrNull()) {
                is Outcome.Success ->
                    emit(ResponseWrapper.Succeed(outcome.products))
                is Outcome.Gated ->
                    emit(ResponseWrapper.Succeed(outcome.gate.await()))
                Outcome.Failure, null ->
                    emit(ResponseWrapper.Failed())
            }
        } finally {
            if (!currentCoroutineContext().isActive)
                cancelledQueries += searchQuery
        }
    }

    private sealed interface Outcome {
        data class Success(val products: List<PreviewProductSummary>) : Outcome
        data class Gated(val gate: CompletableDeferred<List<PreviewProductSummary>>) : Outcome
        data object Failure : Outcome
    }
}
