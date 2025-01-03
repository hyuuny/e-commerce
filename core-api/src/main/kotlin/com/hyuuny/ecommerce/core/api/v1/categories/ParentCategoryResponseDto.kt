import com.hyuuny.ecommerce.core.api.v1.categories.ChildCategoryData
import com.hyuuny.ecommerce.core.api.v1.categories.ParentCategoryData

data class ParentCategoryResponseDto(
    val id: Long,
    val name: String,
    val iconImageUrl: String,
    val hide: Boolean,
) {
    constructor(parentCategory: ParentCategoryData) : this(
        id = parentCategory.id,
        name = parentCategory.name,
        iconImageUrl = parentCategory.iconImageUrl,
        hide = parentCategory.hide,
    )
}

data class ChildCategoryResponseDto(
    val id: Long,
    val parentId: Long,
    val name: String,
    val hide: Boolean,
) {
    constructor(childCategoryData: ChildCategoryData) : this(
        id = childCategoryData.id,
        parentId = childCategoryData.parentId,
        name = childCategoryData.name,
        hide = childCategoryData.hide,
    )
}
