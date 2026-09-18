package com.example.generator

import com.example.model.BlitzQuestion
import kotlin.random.Random

object DailyBlitzGenerator {

    fun generateQuestion(difficultyLevel: Int): BlitzQuestion {
        val rand = Random.Default
        val type = rand.nextInt(5)

        return when (type) {
            0 -> generateMissingAddSub(difficultyLevel, rand)
            1 -> generateMissingMulDiv(difficultyLevel, rand)
            2 -> generateOrderOfOps(difficultyLevel, rand)
            3 -> generateNumberSequence(difficultyLevel, rand)
            else -> generateMentalMath(difficultyLevel, rand)
        }
    }

    private fun generateMissingAddSub(level: Int, rand: Random): BlitzQuestion {
        val maxNum = 20 + level * 5
        val a = rand.nextInt(5, maxNum)
        val b = rand.nextInt(3, maxNum)
        val isAdd = rand.nextBoolean()

        if (isAdd) {
            val sum = a + b
            // "a + ? = sum"
            val correct = b
            val options = makeOptions(correct, rand)
            return BlitzQuestion(
                id = "blitz_${System.nanoTime()}",
                prompt = "$a  +  [ ? ]  =  $sum",
                options = options,
                correctIndex = options.indexOf(correct),
                explanation = "$sum - $a = $correct"
            )
        } else {
            val total = a + b
            // "total - ? = a"
            val correct = b
            val options = makeOptions(correct, rand)
            return BlitzQuestion(
                id = "blitz_${System.nanoTime()}",
                prompt = "$total  -  [ ? ]  =  $a",
                options = options,
                correctIndex = options.indexOf(correct),
                explanation = "$total - $a = $correct"
            )
        }
    }

    private fun generateMissingMulDiv(level: Int, rand: Random): BlitzQuestion {
        val a = rand.nextInt(3, 10 + (level / 2))
        val b = rand.nextInt(2, 10 + (level / 2))
        val product = a * b
        val isMul = rand.nextBoolean()

        if (isMul) {
            // "? × b = product"
            val correct = a
            val options = makeOptions(correct, rand)
            return BlitzQuestion(
                id = "blitz_${System.nanoTime()}",
                prompt = "[ ? ]  ×  $b  =  $product",
                options = options,
                correctIndex = options.indexOf(correct),
                explanation = "$product ÷ $b = $correct"
            )
        } else {
            // "product ÷ ? = a"
            val correct = b
            val options = makeOptions(correct, rand)
            return BlitzQuestion(
                id = "blitz_${System.nanoTime()}",
                prompt = "$product  ÷  [ ? ]  =  $a",
                options = options,
                correctIndex = options.indexOf(correct),
                explanation = "$product ÷ $a = $correct"
            )
        }
    }

    private fun generateOrderOfOps(level: Int, rand: Random): BlitzQuestion {
        val a = rand.nextInt(2, 9)
        val b = rand.nextInt(2, 8)
        val c = rand.nextInt(2, 7)
        val correct = a + (b * c)
        val distractorOrder = (a + b) * c

        val set = mutableSetOf(correct, distractorOrder)
        while (set.size < 4) {
            val offset = rand.nextInt(-6, 7)
            if (offset != 0) set.add(maxOf(1, correct + offset))
        }
        val options = set.toList().shuffled()

        return BlitzQuestion(
            id = "blitz_${System.nanoTime()}",
            prompt = "$a  +  $b  ×  $c  =  ?",
            options = options,
            correctIndex = options.indexOf(correct),
            explanation = "Multiplication first: $b × $c = ${b * c}, then + $a = $correct"
        )
    }

    private fun generateNumberSequence(level: Int, rand: Random): BlitzQuestion {
        val start = rand.nextInt(2, 12)
        val step = rand.nextInt(2, 6)
        val isGeometric = level > 3 && rand.nextBoolean() && start <= 4 && step <= 3

        val seq: List<Int>
        val correct: Int
        val expl: String

        if (isGeometric) {
            val ratio = rand.nextInt(2, 4)
            seq = listOf(start, start * ratio, start * ratio * ratio)
            correct = start * ratio * ratio * ratio
            expl = "Rule: multiply by $ratio each time (${seq.last()} × $ratio = $correct)"
        } else {
            seq = listOf(start, start + step, start + step * 2, start + step * 3)
            correct = start + step * 4
            expl = "Rule: add $step each time (${seq.last()} + $step = $correct)"
        }

        val options = makeOptions(correct, rand)
        return BlitzQuestion(
            id = "blitz_${System.nanoTime()}",
            prompt = "${seq.joinToString(", ")} , [ ? ]",
            options = options,
            correctIndex = options.indexOf(correct),
            explanation = expl
        )
    }

    private fun generateMentalMath(level: Int, rand: Random): BlitzQuestion {
        val base = rand.nextInt(4, 15)
        val correct = base * base
        val options = makeOptions(correct, rand)
        return BlitzQuestion(
            id = "blitz_${System.nanoTime()}",
            prompt = "$base²  =  ?",
            options = options,
            correctIndex = options.indexOf(correct),
            explanation = "$base × $base = $correct"
        )
    }

    private fun makeOptions(correct: Int, rand: Random): List<Int> {
        val set = mutableSetOf(correct)
        var tries = 0
        while (set.size < 4 && tries < 30) {
            tries++
            val delta = rand.nextInt(-5, 6)
            if (delta != 0 && (correct + delta) >= 0) {
                set.add(correct + delta)
            }
        }
        while (set.size < 4) {
            set.add(set.maxOrNull()!! + 1)
        }
        return set.toList().shuffled()
    }
}
