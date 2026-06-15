package com.abster.home.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abster.home.domain.model.RouterAbstraction

@Composable
fun InterfaceCustomizationScreen(
    abstraction: RouterAbstraction,
    onUpdate: (RouterAbstraction) -> Unit,
    onBack: () -> Unit
) {
    var tempAbstraction by remember { mutableStateOf(abstraction) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Interface Customization", style = MaterialTheme.typography.titleLarge)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Map hardware identifiers to human-friendly names.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(tempAbstraction.interfaces.toList()) { (id, label) ->
                CustomizationItem(id, label) { newLabel ->
                    tempAbstraction = tempAbstraction.copy(
                        interfaces = tempAbstraction.interfaces.toMutableMap().apply { put(id, newLabel) }
                    )
                }
            }

            items(tempAbstraction.radios.toList()) { (id, label) ->
                CustomizationItem(id, label) { newLabel ->
                    tempAbstraction = tempAbstraction.copy(
                        radios = tempAbstraction.radios.toMutableMap().apply { put(id, newLabel) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onUpdate(tempAbstraction); onBack() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Customizations")
                }
            }
        }
    }
}

@Composable
fun CustomizationItem(id: String, currentLabel: String, onLabelChange: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(id, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            OutlinedTextField(
                value = currentLabel,
                onValueChange = onLabelChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp)) },
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}
