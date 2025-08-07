package com.example.ginrummy

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import android.view.animation.*
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameView = findViewById(R.id.game_view)
        val spinner: Spinner = findViewById(R.id.difficulty_spinner)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                pos: Int,
                id: Long
            ) {
                val difficulty = when (pos) {
                    0 -> AIDifficulty.EASY
                    1 -> AIDifficulty.MEDIUM
                    else -> AIDifficulty.HARD
                }
                gameView.setDifficulty(difficulty)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}

enum class AIDifficulty {
    EASY, MEDIUM, HARD
}

data class Card(val suit: String, val value: String)

class Deck {
    private val suits = listOf("♠", "♥", "♦", "♣")
    private val values = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    val cards = mutableListOf<Card>()

    init {
        for (suit in suits) {
            for (value in values) {
                cards.add(Card(suit, value))
            }
        }
        shuffle()
    }

    fun shuffle() {
        cards.shuffle()
    }

    fun draw(): Card = cards.removeAt(0)
}

class GinRummyGame(private val difficulty: AIDifficulty) {
    val deck = Deck()
    val playerHand = mutableListOf<Card>();
    val aiHand = mutableListOf<Card>();

    init {
        repeat(10) {
            playerHand.add(deck.draw())
            aiHand.add(deck.draw())
        }
    }
}

class GameView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var game: GinRummyGame = GinRummyGame(AIDifficulty.EASY)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 40f
        textAlign = Paint.Align.CENTER
    }

    fun setDifficulty(difficulty: AIDifficulty) {
        game = GinRummyGame(difficulty)
        fadeIn()
        invalidate()
    }

    private fun fadeIn() {
        val anim = AlphaAnimation(0f, 1f)
        anim.duration = 500
        anim.fillAfter = true
        this.startAnimation(anim)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val spacing = 120
        val centerX = width / 2
        val yPlayer = height - 200
        val yAI = 200

        for ((i, card) in game.playerHand.withIndex()) {
            drawCard(canvas, card, centerX + (i - 5) * spacing, yPlayer)
        }

        for ((i, _) in game.aiHand.withIndex()) {
            drawCard(canvas, Card("🂠", ""), centerX + (i - 5) * spacing, yAI)
        }
    }

    private fun drawCard(canvas: Canvas, card: Card, x: Int, y: Int) {
        paint.color = Color.WHITE
        canvas.drawRect(x - 50f, y - 70f, x + 50f, y + 70f, paint)
        paint.color = Color.BLACK
        canvas.drawText("${card.value}${card.suit}", x.toFloat(), y.toFloat(), paint)
    }

    fun shuffleAnimation() {
        val anim = TranslateAnimation(0f, 0f, 0f, -50f)
        anim.duration = 300
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = 1
        startAnimation(anim)
    }

    fun slideCardAnimation() {
        val anim = TranslateAnimation(-200f, 0f, 0f, 0f)
        anim.duration = 500
        startAnimation(anim)
    }
}
