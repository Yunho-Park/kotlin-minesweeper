package domain

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class FieldTest {
    class TestSelector(private val positions: MutableList<Position>) : PositionSelector {
        override fun selectPosition() = positions.removeFirst()
    }

    @ParameterizedTest
    @CsvSource(value = ["0,1", "1,0", "-1,1", "1,-1"])
    fun `필드 사이즈가 0 또는 음수일 경우 예외를 던진다`(width: Int, height: Int) {
        Assertions.assertThatThrownBy { Field(width, height) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("입력값은 양의 정수여야 합니다.")
    }

    @Test
    fun `필드에 지뢰를 세팅한다`() {
        val position = Position(0, 0)
        val field = Field(10, 10)
        field.setMine(TestSelector(mutableListOf(position)))

        assertThat(field.cells[position.y][position.x].isMine).isTrue()
    }

    /**
     * 2 X 3
     * 2 X X
     * 1 2 2
     */
    @Test
    fun `필드에 힌트를 세팅한다`() {
        val field = Field(3, 3)
        val mines = mutableListOf(
            Position(1, 0),
            Position(1, 1),
            Position(2, 1),
        )

        repeat(mines.size) {
            field.setMine(TestSelector(mines))
        }
        field.setHints()

        assertAll(
            { assertThat(field.cells[0][0].hint).isEqualTo(2) },
            { assertThat(field.cells[0][2].hint).isEqualTo(3) },
            { assertThat(field.cells[1][0].hint).isEqualTo(2) },
            { assertThat(field.cells[2][0].hint).isEqualTo(1) },
            { assertThat(field.cells[2][1].hint).isEqualTo(2) },
            { assertThat(field.cells[2][2].hint).isEqualTo(2) },
        )
    }
}
