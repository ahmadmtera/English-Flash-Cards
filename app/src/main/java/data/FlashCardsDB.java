package data;

import java.util.ArrayList;

public class FlashCardsDB {
    private ArrayList<FlashCard> flashCardsArrayList;

    public FlashCardsDB() {
        flashCardsArrayList = new ArrayList<>();
        flashCardsArrayList.add(new FlashCard("(v.) renege", "to back out of a promise or commitment"));
        flashCardsArrayList.add(new FlashCard("(adj.) tumid", "enlarged or swollen"));
        flashCardsArrayList.add(new FlashCard("(adj.) doughty", "brave & persistent"));
        flashCardsArrayList.add(new FlashCard("(adj.) prink", "spend time making minor adjustments to one's appearance"));
        flashCardsArrayList.add(new FlashCard("(adv.) defray", "to provide money for an expense"));
    }

    public ArrayList<FlashCard> getFlashCardsArrayList() {
        return flashCardsArrayList;
    }
}
