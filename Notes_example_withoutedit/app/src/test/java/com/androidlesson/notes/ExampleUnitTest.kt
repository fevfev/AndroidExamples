package com.androidlesson.notes

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class NotesAppTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addNote_displaysInList() {
        composeTestRule.setContent {
            NotesApp()
        }

        composeTestRule.onNodeWithText("Введите заметку").performTextInput("New Note")
        composeTestRule.onNodeWithText("Добавить заметку").performClick()
        composeTestRule.onNodeWithText("New Note").assertExists()
    }

    @Test
    fun addEmptyNote_doesNotDisplayInList() {
        composeTestRule.setContent {
            NotesApp()
        }

        composeTestRule.onNodeWithText("Добавить заметку").performClick()
        composeTestRule.onNodeWithText("Введите заметку").assertExists()
    }

    @Test
    fun longPressNote_showsDeleteIcon() {
        composeTestRule.setContent {
            NotesApp()
        }

        composeTestRule.onNodeWithText("Введите заметку").performTextInput("Note to Delete")
        composeTestRule.onNodeWithText("Добавить заметку").performClick()
        composeTestRule.onNodeWithText("Note to Delete").performClick()
        composeTestRule.onNodeWithText("Удалить").assertExists()
    }

    @Test
    fun deleteNote_removesFromList() {
        composeTestRule.setContent {
            NotesApp()
        }

        composeTestRule.onNodeWithText("Введите заметку").performTextInput("Note to Delete")
        composeTestRule.onNodeWithText("Добавить заметку").performClick()
        composeTestRule.onNodeWithText("Note to Delete").performClick()
        composeTestRule.onNodeWithText("Удалить").performClick()
        composeTestRule.onNodeWithText("Note to Delete").assertDoesNotExist()
    }
}