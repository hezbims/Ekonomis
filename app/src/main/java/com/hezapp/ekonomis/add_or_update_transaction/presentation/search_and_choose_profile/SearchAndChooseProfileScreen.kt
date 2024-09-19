package com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.MyScaffold
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.core.domain.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.entity.support_enum.TransactionType
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader
import com.hezapp.ekonomis.core.presentation.model.MyScaffoldState
import com.hezapp.ekonomis.core.presentation.utils.getProfileStringId

@Composable
fun SearchAndChooseProfileScreen(
    transactionType: TransactionType,
    onSelectProfile: (ProfileEntity) -> Unit,
    navController: NavHostController,
) {
    val viewModel = viewModel<SearchAndChooseProfileViewModel>(
        factory = SearchAndChooseProfileViewModel.Factory(transactionType)
    )

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.onEvent(SearchAndChooseProfileEvent.LoadAvailableProfiles)
    }
    val scaffoldState = remember {
        MyScaffoldState().withTitleText(
            context.getString(
                R.string.choose_profile_label,
                context.getString(transactionType.getProfileStringId())
            )
        )
    }

    MyScaffold(
        scaffoldState = scaffoldState,
        navController = navController,
    ) {
        SearchAndChooseProfileScreen(
            onSelectProfile = onSelectProfile,
            state = viewModel.state.collectAsState().value,
            onEvent = viewModel::onEvent,
        )
    }
}

@Composable
private fun SearchAndChooseProfileScreen(
    onSelectProfile: (ProfileEntity) -> Unit,
    state: SearchAndChooseProfileUiState,
    onEvent : (SearchAndChooseProfileEvent) -> Unit,
){
    val personTypeString = stringResource(state.transactionType.getProfileStringId())
    var showCreateNewPersonBottomSheet by rememberSaveable { mutableStateOf(false) }
    val secondaryColor = MaterialTheme.colorScheme.secondary

    val createNewPersonLabel = remember {
        buildAnnotatedString {
            append("Nama ${personTypeString.replaceFirstChar { it.lowercase() }} tidak ketemu?\n")
            withLink(
                link = LinkAnnotation.Clickable(
                    tag = "create-new-person",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = secondaryColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                ) {
                    showCreateNewPersonBottomSheet = true
                },
            ) {
                append("Buat baru disini")
            }
        }
    }

    val textFieldFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        textFieldFocus.requestFocus()
    }

    Column(
        modifier = Modifier.padding(
            start = 24.dp, end = 24.dp, top = 6.dp,
        )
    ) {
        TextField(
            value = state.searchQuery,
            onValueChange = { newValue ->
                onEvent(SearchAndChooseProfileEvent.ChangeSearchQuery(newValue))
            },
            label = { Text(stringResource(R.string.search_profile_name_label, personTypeString)) },
            trailingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(textFieldFocus),
        )

        ResponseLoader(
            response = state.availableProfilesResponse,
            onRetry = {
                onEvent(SearchAndChooseProfileEvent.LoadAvailableProfiles)
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) { listProfile ->
            if (listProfile.isEmpty())
                Text(
                    createNewPersonLabel,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            else
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(
                        top = 24.dp, bottom = 64.dp,
                    ),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item {
                        Text(
                            createNewPersonLabel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                    }

                    items(listProfile, key = { data -> data.id }) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectProfile(it)
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    it.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
        }
    }

    CreateNewProfileBottomSheet(
        isShowing = showCreateNewPersonBottomSheet,
        onDismiss = { showCreateNewPersonBottomSheet = false },
        onEvent = onEvent,
        state = state,
        initialProfileName = state.searchQuery,
    )
}