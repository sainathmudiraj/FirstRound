import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class PokerSorter {
    // store one player hand values
    ArrayList<Integer> cards;
    // store one player hand suits
    ArrayList<Character> suits;
    // translate values string to int
    Dictionary<String, Integer> cardsDictionary;
    // one player cards line
    String hand;
    public static void main(String[] args){
        // buffer to read input lines
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));   
        PokerSorter obj = new PokerSorter();
        obj.initializeCardsDict();
        // store one player winning cards values temp
        ArrayList<Integer> values = new ArrayList<Integer>(5);
        ArrayList<Integer> values1 = new ArrayList<Integer>(5);
        ArrayList<Integer> values2 = new ArrayList<Integer>(5);
        // store one player all hand cards
        ArrayList<Integer> cards1 = new ArrayList<Integer>(5);
        ArrayList<Integer> cards2 = new ArrayList<Integer>(5);
        int ranks[] = new int[2];
        int scores[] = new int[2];
        // two players cards line
        String twoHands;

        try {
            // get first line
            twoHands = buffer.readLine();
            // not ctrl-D
            while(twoHands != null){   

                for (int player = 0; player < 2; player++)
                {
                    obj.hand = twoHands.substring(player*15, player*15 + 14);
                    obj.setCardsSuits(); // translate one player hand to cards and suits

                    if ((values = obj.isStraightFlush(10)) != null){
                        ranks[player] = 10;
                    } else if ((values = obj.isStraightFlush()) != null){
                        ranks[player] = 9;
                    } else if ((values = obj.isCount(4)) != null){
                        ranks[player] = 8;
                    } else if ((values = obj.isFullHouse()) != null){
                        ranks[player] = 7;
                    } else if ((values = obj.isFlush()) != null){
                        ranks[player] = 6;
                    } else if ((values = obj.isStraight()) != null){
                        ranks[player] = 5;
                    } else if ((values = obj.isCount(3)) != null){
                        ranks[player] = 4;
                    } else if ((values = obj.isTwoPairs()) != null){
                        ranks[player] = 3;
                    } else if ((values = obj.isCount(2)) != null){
                        ranks[player] = 2;
                    }
                    else {
                        values = obj.cards;
                        ranks[player] = 1;
                    }

                    if (player == 0){
                        values1 = values;
                        cards1 = obj.cards;
                    }
                    else {
                        values2 = values;
                        cards2 = obj.cards;
                    }
                }

                resolveTie(ranks, values1, values2);
                resolveTie(ranks, cards1, cards2);

                if (ranks[0] > ranks[1]){
                    scores[0]++;
                } else {
                    scores[1]++;
                }
                // read next line
                twoHands = buffer.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 

        System.out.print("Player 1: ");
        System.out.print(scores[0]);
        System.out.println(" hands");
        System.out.print("Player 2: ");
        System.out.print(scores[1]);
        System.out.println(" hands");
    }

    private static void resolveTie(int[] ranks, ArrayList<Integer> set1, ArrayList<Integer> set2){
        // resolve tie based on highest card in sets
        while (ranks[0] == ranks[1] && set1.size() != 0){
            int maxCard1 = Collections.max(set1);
            int maxCard2 = Collections.max(set2);
            if (maxCard1 > maxCard2){
                ranks[0]++;
            } else if (maxCard2 > maxCard1){
                ranks[1]++;
            } else {
                // remove all cards equals to highest cards
                set1.removeAll(Arrays.asList(maxCard1));
                set2.removeAll(Arrays.asList(maxCard1));
            }
        }
    }

    private void initializeCardsDict(){
        // translate card value to integers
        cardsDictionary = new Hashtable<String, Integer>();
        cardsDictionary.put("A", 14);
        cardsDictionary.put("K", 13);
        cardsDictionary.put("Q", 12);
        cardsDictionary.put("J", 11);
        cardsDictionary.put("T", 10);
    }

    private void setCardsSuits(){
        // initialize cards and suits
        cards = new ArrayList<Integer>(5);
        suits = new ArrayList<Character>(5);
        String cardValue;
        String cardString[] = hand.split(" "); // array of card strings
        for (int i = 0; i < cardString.length; i++){
            cardValue = cardString[i].substring(0, 1);
            // if value is included in cardsDictionary
            if (((Hashtable<String, Integer>) cardsDictionary).containsKey(cardValue)) {
                cards.add(cardsDictionary.get(cardValue));
            } else { // else it's an integer
                cards.add(Integer.valueOf(cardValue));
            }
            // get card suit
            suits.add(cardString[i].charAt(1));
        }
    }

    private boolean hasCardSuit(int card, char suit){
        // check if card value of specific suit exists in one player hand
        int cardIndx = cards.indexOf(card);
        if (cardIndx > -1){
            if (Character.compare(suits.get(cardIndx), suit) != 0){
                return false;
            }
        }
        else {
            return false;
        }
        return true;
    }

    private char whatSuit(int card){
        // get corresponding suit of card value
        int cardIndx = cards.indexOf(card);
        if (cardIndx > -1){
            return suits.get(cardIndx);
        }
        else{
            return 'N'; // doesn't exist
        }
    }

    private  ArrayList<Integer> isStraightFlush(int start){
        // check stright flush starting from specific card value
        int minCard = start;
        char suit = whatSuit(minCard);
        ArrayList<Integer> values = new ArrayList<Integer>(5);
        if (suit != 'N'){ // start card exists
            values.add(minCard);
            for (int i = 1; i < 5; i++){ // check if all consecutive cards exist
                if (hasCardSuit(minCard+i, suit)){
                    values.add(minCard+i);
                }
                else{
                    return null;
                }
            }
        } else{
            return null;
        }
        if (values.size() == 5){ // all five cards are found
            return values;
        } else {
            return null;
        }
    }

    private ArrayList<Integer> isStraightFlush(){
        // check for straight flush starting from smallest card
        int minCard = Collections.min(cards);
        return isStraightFlush(minCard);
    }

    private ArrayList<Integer> isCount(int count){
        // check existance of dublicated values
        ArrayList<Integer> values = new ArrayList<Integer>(5);
        for (int i = 0; i < cards.size(); i++){ // check all cards
            if (Collections.frequency(cards, cards.get(i)) == count){ // chech card frequency
                values.add(cards.get(i));
                return values;
            }
        }
        return null;
    }

    private ArrayList<Integer> isFullHouse(){
        // get unique set of cards
        HashSet<Integer> hset = new HashSet<Integer>(cards);
        if (hset.size() == 2){ // two unique cards 
            return new ArrayList<Integer>(hset); // cast unique hashset to arrayset
        }
        else {
            return null;
        }
    }

    private ArrayList<Integer> isFlush(){
        // get unique set of suits
        HashSet<Character> hset = new HashSet<Character>(suits);
        if (hset.size() == 1){ // all cards has the same suit
            return cards;
        } else {
            return null;
        }
    }

    private ArrayList<Integer> isStraight(){
        // check for consecutive cards without checking for suit
        int minCard = Collections.min(cards);
        int cardIndx;
        ArrayList<Integer> values = new ArrayList<Integer>(5);
        values.add(minCard);
        for (int i = 1; i < 5; i++){
            cardIndx = cards.indexOf(minCard+i);
            if (cardIndx > -1){
                values.add(minCard+i);
            }
            else{
                return null;
            }
        }
        if (values.size() == 5){
            return values;
        }
        else {
            return null;
        }
    }

    private ArrayList<Integer> isTwoPairs(){
        int pairCount = 0;
        int firstPair = -1;
        ArrayList<Integer> values = new ArrayList<Integer>(5);
        for (int i = 0; i < cards.size(); i++){ // check all cards
            if (firstPair == cards.get(i)){ // if card value was checked before
                continue;
            }
            if (Collections.frequency(cards, cards.get(i)) == 2){ // pair exist
                pairCount++;
                firstPair = cards.get(i);
                values.add(firstPair);
            }
            if (pairCount == 2){ // found two pairs
                return values;
            }
        }
        return null;
    }
}