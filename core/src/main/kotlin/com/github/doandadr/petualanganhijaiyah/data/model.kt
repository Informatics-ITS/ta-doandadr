package com.github.doandadr.petualanganhijaiyah.data

import com.badlogic.gdx.utils.Array
import com.github.doandadr.petualanganhijaiyah.asset.MusicAsset
import com.github.doandadr.petualanganhijaiyah.asset.TextureAsset
import ktx.collections.gdxArrayOf

val levelsData: Array<LevelModel> = gdxArrayOf(
    LevelModel(
        number = 1,
        name = "Level 1",
        isTimed = true,
        timerSeconds = Difficulty.EASY.timerSeconds,
        isHealthCounted = true,
        maxHealth = Difficulty.EASY.maxHealth,
        isScored = true,
        stages = listOf(
            StageModel(
                type = StageType.MCQ,
                choices = Difficulty.EASY.choices,
                rounds = 8
            ),
        ),
        bgIndex = TextureAsset.FOREST_1.ordinal,
        musicIndex = MusicAsset.FIELD.ordinal,
    ),
    LevelModel(
        number = 2,
        name = "Level 2",
        isTimed = true,
        timerSeconds = Difficulty.EASY.timerSeconds,
        isHealthCounted = true,
        maxHealth = Difficulty.EASY.maxHealth,
        isScored = true,
        stages = listOf(
            StageModel(
                type = StageType.MCQ_VOICE,
                choices = Difficulty.EASY.choices,
                rounds = 3
            ),
            StageModel(
                type = StageType.DRAG_AND_DROP,
                choices = Difficulty.EASY.choices,
                rounds = 2
            )
        ),
        bgIndex = TextureAsset.FOREST_1.ordinal,
        musicIndex = MusicAsset.FIELD.ordinal
    ),
    LevelModel(
        number = 3,
        name = "Level 3",
        isTimed = true,
        timerSeconds = Difficulty.EASY.timerSeconds,
        isHealthCounted = true,
        maxHealth = Difficulty.EASY.maxHealth,
        isScored = true,
        stages = listOf(
            StageModel(
                type = StageType.MATCH_LINE,
                choices = Difficulty.EASY.choicesMatch,
                rounds = 2
            ),
            StageModel(
                type = StageType.MCQ,
                choices = Difficulty.EASY.choices,
                rounds = 4
            ),
        ),
        bgIndex = TextureAsset.FOREST_1.ordinal,
        musicIndex = MusicAsset.FIELD.ordinal
    ),
    LevelModel(
        number = 4,
        name = "Level 4",
        isTimed = true,
        timerSeconds = Difficulty.MEDIUM.timerSeconds,
        isHealthCounted = true,
        maxHealth = Difficulty.MEDIUM.maxHealth,
        isScored = true,
        stages = listOf(
            StageModel(
                type = StageType.MCQ_JOIN,
                choices = Difficulty.MEDIUM.choices,
                rounds = 2
            ),
            StageModel(
                type = StageType.MCQ_VOICE,
                choices = Difficulty.MEDIUM.choices,
                rounds = 4
            ),
        ),
        bgIndex = TextureAsset.FOREST_2.ordinal,
        musicIndex = MusicAsset.FIELD.ordinal
    ),
    LevelModel(
        number = 5,
        name = "Level 5",
        isTimed = true,
        timerSeconds = Difficulty.MEDIUM.timerSeconds,
        isHealthCounted = true,
        maxHealth = Difficulty.MEDIUM.maxHealth,
        isScored = true,
        stages = listOf(
            StageModel(
                type = StageType.DRAWING,
                choices = 1,
                rounds = 4
            ),
        ),
        bgIndex = TextureAsset.FOREST_2.ordinal,
        musicIndex = MusicAsset.FIELD.ordinal
    ),
    LevelModel(
        number = 6,
        name = "Level 6",
        isTimed = true,
        timerSeconds = Difficulty.MEDIUM.timerSeconds,
        isHealthCounted = true,
        maxHealth = Difficulty.MEDIUM.maxHealth,
        isScored = true,
        stages = listOf(
            StageModel(
                type = StageType.MCQ_VOICE,
                choices = Difficulty.MEDIUM.choices,
                rounds = 4
            ),
            StageModel(
                type = StageType.MATCH_LINE,
                choices = Difficulty.MEDIUM.choicesMatch,
                rounds = 2
            ),
        ),
        bgIndex = TextureAsset.DESERT_1.ordinal,
        musicIndex = MusicAsset.DESERT_ALT.ordinal
    ),
    LevelModel(
        number = 7,
        name = "Level 7",
        isTimed = true,
        timerSeconds = Difficulty.HARD.timerSeconds,
        isHealthCounted = true,
        maxHealth = Difficulty.HARD.maxHealth,
        isScored = true,
        stages = listOf(
            StageModel(
                type = StageType.DRAG_AND_DROP,
                choices = Difficulty.MEDIUM.choices,
                rounds = 2
            ),
            StageModel(
                type = StageType.MCQ,
                choices = Difficulty.MEDIUM.choices,
                rounds = 3
            ),
            StageModel(
                type = StageType.MCQ_JOIN,
                choices = Difficulty.MEDIUM.choices,
                rounds = 2
            ),
        ),
        bgIndex = TextureAsset.DESERT_1.ordinal,
        musicIndex = MusicAsset.DESERT_ALT.ordinal
    ),
    LevelModel(
        number = 8,
        name = "Level 8",
        isTimed = true,
        timerSeconds = Difficulty.HARD.timerSeconds,
        isHealthCounted = true,
        maxHealth = Difficulty.HARD.maxHealth,
        isScored = true,
        stages = listOf(
            StageModel(
                type = StageType.MCQ,
                choices = Difficulty.HARD.choices,
                rounds = 3
            ),
            StageModel(
                type = StageType.MCQ_VOICE,
                choices = Difficulty.HARD.choices,
                rounds = 3
            ),
            StageModel(
                type = StageType.MATCH_LINE,
                choices = Difficulty.HARD.choicesMatch,
                rounds = 2
            ),
        ),
        bgIndex = TextureAsset.DESERT_2.ordinal,
        musicIndex = MusicAsset.DESERT_ALT.ordinal
    ),
    LevelModel(
        number = 9,
        name = "Level 9",
        isTimed = true,
        timerSeconds = Difficulty.HARD.timerSeconds,
        isHealthCounted = true,
        maxHealth = Difficulty.HARD.maxHealth,
        isScored = true,
        stages = listOf(
            StageModel(
                type = StageType.DRAG_AND_DROP,
                choices = Difficulty.HARD.choices,
                rounds = 1
            ),
            StageModel(
                type = StageType.MCQ_JOIN,
                choices = Difficulty.HARD.choices,
                rounds = 2
            ),
            StageModel(
                type = StageType.DRAWING,
                choices = 1,
                rounds = 2
            ),
        ),
        bgIndex = TextureAsset.DESERT_2.ordinal,
        musicIndex = MusicAsset.DESERT_ALT.ordinal
    ),
)



