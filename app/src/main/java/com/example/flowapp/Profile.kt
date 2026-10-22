package com.example.flowapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun Profile(back: () -> Unit, sheetState: SheetState) {
    ModalBottomSheet(onDismissRequest = {}, sheetState = sheetState) {
        Column {
            Icon(imageVector = Icons.Default.Person, contentDescription = "icon")
            Text("Jonathan Blackwood")
            Button(onClick = back) {
                Text("Logout")
            }
        }
    }
}