package org.bizilabs.vary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalWindowInfo
import org.bizilabs.vary.models.ArrayBasedVaryValueScope
import org.bizilabs.vary.models.LocalVarySize
import org.bizilabs.vary.models.VaryLayoutScope
import org.bizilabs.vary.models.VarySize
import org.bizilabs.vary.models.VarySize.Companion.ordinal
import org.bizilabs.vary.models.VaryValueScope
import org.bizilabs.vary.models.rememberVarySize
import org.bizilabs.vary.models.rememberVarySizeUsingBinary

@Composable
fun Vary(
    width: Int = LocalWindowInfo.current.containerSize.width,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalVarySize provides width) {
        content()
    }
}

@Suppress("ComposableNaming")
@Composable
fun vary(
    builder: @Composable VaryLayoutScope.() -> Unit = {},
    xs: @Composable () -> Unit,
) {
    val width = LocalVarySize.current
    val size = rememberVarySize(width)

    val scope = remember { VaryLayoutScope() }.apply{builder()}

    val composableToRender = remember(size, scope.content) {
        scope.content[VarySize.XS] = xs

        for (i in size.ordinal() downTo 0){
            scope.content[VarySize.all[i]]?.let { return@remember it }
        }
        xs
    }
    composableToRender()

}

@Composable
fun <T> vary(
    xs: T,
    builder: VaryValueScope<T>.() -> Unit = {},
): T {
    val width = LocalVarySize.current
    val size = rememberVarySize(width)

    val scope = remember { VaryValueScope<T>() }.apply(builder)

    return remember(size, scope.values) {
        scope.values[VarySize.XS] = xs
        (VarySize.all.indexOf(size) downTo 0)
            .asSequence()
            .mapNotNull { index -> scope.values[VarySize.all[index]] }
            .firstOrNull() ?: xs
    }
}


@Composable
fun <T> varyArrayBased(
    xs: T,
    builder: ArrayBasedVaryValueScope<T>.() -> Unit = {},
): T {
    val width = LocalVarySize.current
    val size = rememberVarySizeUsingBinary(width)

    val scope = remember { ArrayBasedVaryValueScope<T>() }.apply(builder)


    return remember(size, scope) {

        scope[VarySize.XS] = xs
        for (i in size.ordinal() downTo 0){
            scope.getByOrdinal(i)?.let { return@remember it }
        }
        xs
    }
}
