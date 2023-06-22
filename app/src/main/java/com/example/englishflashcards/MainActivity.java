package com.example.englishflashcards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

import data.FlashCard;

public class MainActivity extends AppCompatActivity {
    MainActivity here = this;
    ArrayList<FlashCard> flashCardsArrayList = new ArrayList<>();
    boolean isFront = true;
    boolean editMode = false;
    int currentFlashCardBeingViewed = 0;
    Button aboutBtn;
    AlertDialog.Builder aboutAppAlertDialog;
    EditText moduleNameEditText;
    Button listAllFlashCardsBtn;
    Button modifyFlashCardsBtn;
    CardView flashCardCardView;
    TextView frontCardTextView;
    TextView backCardTextView;
    ProgressBar progressBar;
    Button prevBtn;
    Button nextBtn;


    /**
     * First method to be called once the MainActivity is brought into the foreground upon opening the app.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        bindXMLToJavaReferences();
        loadSPData();
        setListeners();
    }

    private void setListeners() {
        aboutBtnListener();
        modifyFlashCardsBtnListener();
        viewAllFlashCardsBtnListener();
        flashCardCardViewListener();
        prevBtnListener();
        nextBtnListener();
    }

    private void viewAllFlashCardsBtnListener() {
        listAllFlashCardsBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(here, ListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(getString(R.string.MainActivitySPCardsSet), (Serializable) flashCardsArrayList);
                intent.putExtra(getString(R.string.MainActivitySPCardsSet), bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStop() {
        saveDataToSP(this.flashCardsArrayList);
        super.onStop();
    }

    /**
     * Loading data for the UI.
     */
    private void loadSPData() {
        SharedPreferences sp = super.getSharedPreferences(getString(R.string.MainActivitySP), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sp.getString(getString(R.string.MainActivitySPCardsSet),"");
        Type type = new TypeToken<ArrayList<FlashCard>>() {}.getType();
        if (!json.isEmpty()) {
            ArrayList<FlashCard> flashCards = gson.fromJson(json, type);
            for (int i = 0; i < flashCards.size(); i++) {
                addFlashCard(flashCards.get(i));
            }
        }
    }

    /**
     * Loading data for the UI.
     */
    private void saveDataToSP(ArrayList<FlashCard> flashCards) {
        SharedPreferences sp = super.getSharedPreferences(getString(R.string.MainActivitySP), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(flashCards);
        editor.putString(getString(R.string.MainActivitySPCardsSet), json);
        editor.apply();
    }

    /**
     * Creates a listener to handle the clicking of the next card button.
     */
    private void nextBtnListener() {
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flashCardsArrayList.isEmpty()) {
                    Snackbar.make(view, "No cards added! Touch the pencil icon (at the top right), then the plus icon (at the top left) to add some cards.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                currentFlashCardBeingViewed++;
                if (currentFlashCardBeingViewed >= flashCardsArrayList.size())
                    currentFlashCardBeingViewed = 0;
                FlashCard nextCard = flashCardsArrayList.get(currentFlashCardBeingViewed);
                progressBar.setProgress(currentFlashCardBeingViewed, true);
                frontCardTextView.setText(nextCard.getFront());
                backCardTextView.setText(nextCard.getBack());
            }
        });
    }

    /**
     * Creates a listener to handle the clicking of the previous card button.
     */
    private void prevBtnListener() {
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flashCardsArrayList.isEmpty()) {
                    Snackbar.make(view, "No cards added! Click on the pencil icon (at the top right) to add some cards.", Snackbar.LENGTH_LONG).show();
                    return;
                }
                currentFlashCardBeingViewed--;
                if (currentFlashCardBeingViewed < 0)
                    currentFlashCardBeingViewed = flashCardsArrayList.size() - 1;
                FlashCard prevCard = flashCardsArrayList.get(currentFlashCardBeingViewed);
                progressBar.setProgress(currentFlashCardBeingViewed, true);
                frontCardTextView.setText(prevCard.getFront());
                backCardTextView.setText(prevCard.getBack());
            }
        });
    }

    /**
     * Creates a listener to handle the clicking of the flash card CardView.
     */
    private void flashCardCardViewListener() {
        flashCardCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle deletion when in edit mode
                if (editMode) {
                    deleteCard(currentFlashCardBeingViewed);
                    if (flashCardsArrayList.isEmpty()) {
                        progressBar.setProgress(0, true);
                        progressBar.setMax(0);
                        frontCardTextView.setText(R.string.front_card_placeholder);
                        backCardTextView.setText(R.string.back_card_placeholder);
                        Snackbar.make(view, "No cards left to delete!", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    currentFlashCardBeingViewed--;
                    if (currentFlashCardBeingViewed < 0)
                        currentFlashCardBeingViewed = flashCardsArrayList.size() - 1;
                    FlashCard prevCard = flashCardsArrayList.get(currentFlashCardBeingViewed);
                    progressBar.setProgress(currentFlashCardBeingViewed, true);
                    progressBar.setMax(flashCardsArrayList.size());
                    frontCardTextView.setText(prevCard.getFront());
                    backCardTextView.setText(prevCard.getBack());
                    Snackbar.make(view, "Card deleted.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                // Otherwise, do the flip animation.
                float scale = getApplicationContext().getResources().getDisplayMetrics().density;

                frontCardTextView.setCameraDistance(8000 * scale);
                backCardTextView.setCameraDistance(8000 * scale);

                Animator frontAnimator = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.front_animator);
                Animator backAnimator = AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.back_animator);

                if (isFront) {
                    frontAnimator.setTarget(frontCardTextView);
                    backAnimator.setTarget(backCardTextView);
                    backCardTextView.setVisibility(View.VISIBLE);
                    frontAnimator.start();
                    backAnimator.start();
                    isFront = false;
                } else {
                    frontAnimator.setTarget(backCardTextView);
                    backAnimator.setTarget(frontCardTextView);
                    frontCardTextView.setVisibility(View.VISIBLE);
                    backAnimator.start();
                    frontAnimator.start();
                    isFront = true;
                }
            }
        });
    }

    /**
     * Creates a listener to handle the clicking of the modify button.
     */
    private void modifyFlashCardsBtnListener() {
        modifyFlashCardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMode) {
                    editMode = false;
                    listAllFlashCardsBtn.setVisibility(View.GONE);
                    moduleNameEditText.setVisibility(View.VISIBLE);
                    modifyFlashCardsBtn.setText(R.string.pencil_symbol);
                    modifyFlashCardsBtn.setTextColor(getResources().getColor(R.color.dark_silver));
                    aboutBtn.setText(R.string.info_symbol);
                    aboutBtn.setTextColor(getResources().getColor(R.color.dark_silver));
                    flashCardCardView.setBackgroundColor(getResources().getColor(R.color.light_silver));
                    Toast.makeText(getApplicationContext(), "Edit mode disabled.", Toast.LENGTH_SHORT).show();
                    return;
                }
                editMode = true;
                moduleNameEditText.setVisibility(View.GONE);
                listAllFlashCardsBtn.setVisibility(View.VISIBLE);
                aboutBtn.setText(R.string.plus_symbol);
                aboutBtn.setTextColor(getResources().getColor(R.color.light_green));
                modifyFlashCardsBtn.setText(R.string.check_symbol);
                modifyFlashCardsBtn.setTextColor(getResources().getColor(R.color.light_green));
                flashCardCardView.setBackgroundColor(getResources().getColor(R.color.crimson_red));
                Toast.makeText(getApplicationContext(), "Edit mode enabled.", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Hit the green check once you finish making the necessary changes.", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Deletes flash card at index, adjusting the progress bar appropriately.
     *
     * @param index
     */
    private boolean deleteCard(int index) {
        if (flashCardsArrayList == null || flashCardsArrayList.isEmpty() || index >= flashCardsArrayList.size())
            return false;
        flashCardsArrayList.remove(index);
        progressBar.setMax(flashCardsArrayList.size());
        return true;
    }

    /**
     * Creates a listener to handle the clicking of the about button.
     */
    private void aboutBtnListener() {
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMode) {
                    // Add-card dialog
                    EditText newWordEditText = new EditText(here);
                    newWordEditText.setHint("Word");
                    newWordEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    EditText newWordDescriptionEditText = new EditText(here);
                    newWordDescriptionEditText.setHint("Description");
                    newWordDescriptionEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(here);
                    linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
                    linearLayoutCompat.addView(newWordEditText);
                    linearLayoutCompat.addView(newWordDescriptionEditText);

                    aboutAppAlertDialog = new AlertDialog.Builder(here, 0);
                    AlertDialog dialog = aboutAppAlertDialog.setTitle("Add a New Word:").setView(linearLayoutCompat).setCancelable(false).setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (newWordEditText.getText().toString().isEmpty()) {
                                Toast.makeText(getApplicationContext(), "Type a word in the word field to continue.", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (newWordDescriptionEditText.getText().toString().isEmpty()) {
                                Toast.makeText(getApplicationContext(), "Type a word in the description field to continue.", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                // If data is entered, add it and go back.
                                Toast.makeText(getApplicationContext(), "Success! Word added.", Toast.LENGTH_SHORT).show();
                                addFlashCard(new FlashCard(newWordEditText.getText().toString(), newWordDescriptionEditText.getText().toString()));
                            }
                        }
                    }).show();
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(Color.BLACK);
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEms(10);
                    dialog.setIcon(R.drawable.flash_cards_app_icon);

                    return;
                }

                // About dialog
                aboutAppAlertDialog = new AlertDialog.Builder(here, 0);
                AlertDialog dialog = aboutAppAlertDialog.setTitle("About").setMessage(getResources().getString(R.string.about_description)).setCancelable(true).setNegativeButton("I'm Ready!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(Color.BLACK);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEms(20);
                dialog.setIcon(R.drawable.flash_cards_app_icon);

            }
        });
    }

    /**
     * Adds a flash card to the arraylist, adjusting the progress bar appropriately.
     *
     * @param card
     */
    private void addFlashCard(FlashCard card) {
        if (flashCardsArrayList == null)
            flashCardsArrayList = new ArrayList<>();
        flashCardsArrayList.add(card);
        progressBar.setMax(flashCardsArrayList.size() - 1);
    }

    /**
     * Adds a flash card to the arraylist, adjusting the progress bar appropriately.
     *
     * @param card
     */
    private void insertFlashCard(FlashCard card, int index) {
        if (flashCardsArrayList == null)
            flashCardsArrayList = new ArrayList<>();
        flashCardsArrayList.add(index, card);
        progressBar.setMax(flashCardsArrayList.size() - 1);
    }

    /**
     * Binds the XML elements to their respective Java references.
     */
    private void bindXMLToJavaReferences() {
        aboutBtn = findViewById(R.id.aboutBtn);
        aboutAppAlertDialog = new AlertDialog.Builder(this, 0);
        moduleNameEditText = findViewById(R.id.moduleNameEditText);
        listAllFlashCardsBtn = findViewById(R.id.listAllFlashCardsBtn);
        modifyFlashCardsBtn = findViewById(R.id.modifyFlashCardsBtn);
        flashCardCardView = findViewById(R.id.flashCardCardView);
        frontCardTextView = findViewById(R.id.frontCardTextView);
        backCardTextView = findViewById(R.id.backCardTextView);
        progressBar = findViewById(R.id.progressBar);
        prevBtn = findViewById(R.id.prevBtn);
        nextBtn = findViewById(R.id.nextBtn);
    }

    /**
     * Hide the software keyboard when user clicks anywhere on the screen while the keyboard is prominent.
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            moduleNameEditText.clearFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

}
