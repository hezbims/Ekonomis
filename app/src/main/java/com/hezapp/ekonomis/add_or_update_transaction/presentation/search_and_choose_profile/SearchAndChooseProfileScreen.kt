package com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hezapp.ekonomis.MyScaffold
import com.hezapp.ekonomis.R
import com.hezapp.ekonomis.add_or_update_transaction.presentation.search_and_choose_profile.component.CreateNewProfileBottomSheet
import com.hezapp.ekonomis.core.domain.general_model.ResponseWrapper
import com.hezapp.ekonomis.core.domain.invoice.entity.TransactionType
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileEntity
import com.hezapp.ekonomis.core.domain.profile.entity.ProfileType
import com.hezapp.ekonomis.core.presentation.component.ResponseLoader
import com.hezapp.ekonomis.core.presentation.model.MyScaffoldState
import com.hezapp.ekonomis.core.presentation.preview.PreviewKoin
import com.hezapp.ekonomis.core.presentation.utils.getProfileStringId
import com.hezapp.ekonomis.ui.theme.EkonomisTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SearchAndChooseProfileScreen(
    transactionType: TransactionType,
    onSelectProfile: (ProfileEntity) -> Unit,
    navController: NavHostController,
) {
    val viewModel = koinViewModel<SearchAndChooseProfileViewModel>(
        parameters = { parametersOf(transactionType) }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchAndChooseProfileScreen(
    onSelectProfile: (ProfileEntity) -> Unit,
    state: SearchAndChooseProfileUiState,
    onEvent : (SearchAndChooseProfileEvent) -> Unit,
){
    val personTypeString = stringResource(state.transactionType.getProfileStringId())
    var showCreateNewPersonBottomSheet by rememberSaveable { mutableStateOf(false) }
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val context = LocalContext.current

    val personNotFoundLabel = remember {
        buildAnnotatedString {
            append(context.getString(
                R.string.profile_name_not_found,
                personTypeString.lowercase()))
            append(" ")
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
                append(context.getString(R.string.create_new_here_label))
            }
        }
    }
    val personListEmptyLabel = remember {
        buildAnnotatedString {
            append(context.getString(
                R.string.there_is_no_profile_registered,
                personTypeString.lowercase()))
            append(".\n")
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
                append(context.getString(R.string.create_new_here_label))
            }
        }
    }

    val textFieldFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        textFieldFocus.requestFocus()
    }

    Column(
        modifier = Modifier
            .padding(
                start = 24.dp, end = 24.dp, top = 6.dp,
            )
            .imePadding()
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
                .fillMaxWidth()
                .imeNestedScroll(),
        ) { listProfile ->
            if (listProfile.isEmpty())
                Text(
                    if (state.searchQuery.isEmpty())
                        personListEmptyLabel
                    else
                        personNotFoundLabel,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(
                        top = 24.dp, bottom = 48.dp,
                    ),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item {
                        Text(
                            personNotFoundLabel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    bottom = 12.dp
                                )
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
    }

    CreateNewProfileBottomSheet(
        isShowing = showCreateNewPersonBottomSheet,
        onDismiss = { showCreateNewPersonBottomSheet = false },
        onEvent = onEvent,
        state = state,
        initialProfileName = state.searchQuery,
    )
}

@Preview
@Composable
fun PreviewSearchAndChooseProfileScreenWithProfiles(){
    EkonomisTheme  {
        Surface {
            SearchAndChooseProfileScreen(
                onSelectProfile = { },
                state = SearchAndChooseProfileUiState(
                    availableProfilesResponse = ResponseWrapper.Succeed(listOf(
                        ProfileEntity(id = 2, name = "Bu Metri", type = ProfileType.CUSTOMER),
                        ProfileEntity(id = 1, name = "Bu Sri", type = ProfileType.CUSTOMER),
                    )),
                    transactionType = TransactionType.PENJUALAN),
                onEvent = {},
            )
        }
    }
}

@Preview
@Composable
fun PreviewSearchAndChooseProfileScreenWithNoProfileFound(){
    PreviewKoin {
        SearchAndChooseProfileScreen(
            onSelectProfile = { },
            state = SearchAndChooseProfileUiState(
                availableProfilesResponse = ResponseWrapper.Succeed(emptyList()),
                transactionType = TransactionType.PENJUALAN),
            onEvent = {},
        )
    }
}

// TODO: buat preview ketika melakukan search tapi enggak ada nama yang ketemu