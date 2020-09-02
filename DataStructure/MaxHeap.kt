package datastructure

class MaxHeap {
    private var values = arrayOfNulls<Int>(100)

    var size: Int = 0
        private set

    fun add(value: Int): Int {
        val newIndex = ++size
        values[newIndex] = value
        rearrangeForInsertionFrom(newIndex)
        return value
    }

    private tailrec fun rearrangeForInsertionFrom(index: Int) {
        if (index == 1) return
        val parentIndex = parentIndexOf(index)
        val parent = valueOf(parentIndex)
        val current = valueOf(index)
        if (parent < current) {
            swap(parentIndex, index)
            rearrangeForInsertionFrom(parentIndex)
        }
    }

    fun delete(): Int {
        require(size > 0) { "힙이 비어있습니다" }
        val maxValue = valueOf(1)
        values[1] = null
        swap(1, size--)
        rearrangeForDeleteFrom(1)
        return maxValue
    }

    private tailrec fun rearrangeForDeleteFrom(index: Int) {
        val leftIndex = leftIndexOf(index)
        val rightIndex = rightIndexOf(index)
        val current = valueOf(index)
        val left = values[leftIndex] ?: return
        val right = values[rightIndex] ?: return
        if (left > current || right > current) {
            val targetSwapIndex = if (left > right) leftIndex else rightIndex
            swap(index, targetSwapIndex)
            rearrangeForDeleteFrom(targetSwapIndex)
        }
    }

    private fun swap(i: Int, j: Int) {
        val temp = values[i]
        values[i] = values[j]
        values[j] = temp
    }

    private fun valueOf(index: Int): Int = values[index]
        ?: throw IllegalStateException("$index 번 째의 값이 null 일 수 없음")

    private fun parentIndexOf(index: Int): Int = index shr 1

    private fun leftIndexOf(index: Int): Int = index shl 1

    private fun rightIndexOf(index: Int): Int = leftIndexOf(index) + 1

    override fun toString(): String {
        return values.toList().toString()
    }
}
