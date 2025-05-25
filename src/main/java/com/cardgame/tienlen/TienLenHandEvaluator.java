package com.cardgame.tienlen;

import com.cardgame.core.Card;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.cardgame.core.HandEvaluator;

/**
 * Handles move validation and card comparison logic for Tiến Lên.
 * All rules for valid moves, comparing hands, etc. should be implemented here.
 */
public class TienLenHandEvaluator extends HandEvaluator{
    /**
     * Enum đại diện cho các loại bộ bài Tiến Lên
     */
    public enum MoveType {
        SINGLE, PAIR, TRIPLE, FOUR_OF_A_KIND, SEQUENCE, DOUBLE_SEQUENCE, INVALID
    }

    /**
     * Compare two single cards according to Tiến Lên rules.
     * @param c1 Player's card
     * @param c2 Card on the table
     * @return true if c1 is greater than c2
     */
    public boolean isGreater(Card c1, Card c2) {
        int rank1 = getTienLenRank(c1);
        int rank2 = getTienLenRank(c2);
        if (rank1 != rank2) {
            return rank1 > rank2;
        }
        return c1.getSuitOrder() > c2.getSuitOrder();
    }

    /**
     * Convert card rank to Tiến Lên order (3 is lowest, 2 is highest).
     */
    public int getTienLenRank(Card card) {
        int value = card.getValue(); // 2=2, 3=3,...,10=10, J=11, Q=12, K=13, A=14
        if (value == 2) return 15;
        if (value == 1) return 14; // Ace
        return value;
    }

    /**
     * Nhận diện loại bộ bài (MoveType) dựa trên danh sách lá bài.
     */
    public MoveType detectMoveType(List<Card> cards) {
        if (cards == null || cards.isEmpty()) return MoveType.INVALID;
        if (cards.size() == 1) return MoveType.SINGLE;
        if (super.isFourOfAKind(cards)) return MoveType.FOUR_OF_A_KIND;
        if (super.isThreeOfAKind(cards)) return MoveType.TRIPLE;
        if (super.isPair(cards)) return MoveType.PAIR;
        if (isDoubleSequence(cards)) return MoveType.DOUBLE_SEQUENCE;
        if (super.isStraight(cards)) return MoveType.SEQUENCE;
        return MoveType.INVALID;
    }

    /**
     * Kiểm tra bộ có phải là đôi thông (double sequence, >=6 lá, là các đôi liên tiếp, không chứa 2)
     */
    public boolean isDoubleSequence(List<Card> cards) {
        if (cards == null || cards.size() < 6 || cards.size() % 2 != 0) return false;
        // Sắp xếp bài theo thứ tự Tiến Lên
        List<Card> sorted = new java.util.ArrayList<>(cards);
        sorted.sort((a, b) -> Integer.compare(getTienLenRank(a), getTienLenRank(b)));
        for (int i = 0; i < sorted.size(); i += 2) {
            if (getTienLenRank(sorted.get(i)) != getTienLenRank(sorted.get(i + 1))) return false;
            if (getTienLenRank(sorted.get(i)) == 15) return false; 
            if (i > 0 && getTienLenRank(sorted.get(i)) - getTienLenRank(sorted.get(i - 2)) != 1) return false;
        }
        return true;
    }

    /**
     * Kiểm tra có phải 3 đôi thông (6 lá, 3 đôi liên tiếp)
     */
    public boolean isThreeConsecutivePairs(List<Card> cards) {
        return isDoubleSequence(cards) && cards.size() == 6;
    }

    /**
     * Kiểm tra có phải 4 đôi thông (8 lá, 4 đôi liên tiếp)
     */
    public boolean isFourConsecutivePairs(List<Card> cards) {
        return isDoubleSequence(cards) && cards.size() == 8;
    }

    /**
     * Trả về MoveType tốt nhất của bộ bài (phục vụ cho HandEvaluator API)
     */
    @Override
    public Object evaluateBestHand(List<Card> cards) {
        return detectMoveType(cards);
    }

    /**
     * So sánh hai bộ bài cùng loại, trả về >0 nếu hand1 lớn hơn, <0 nếu hand2 lớn hơn, 0 nếu bằng nhau.
     */
    @Override
    public boolean compareHands(List<Card> cards1, List<Card> cards2) {
        MoveType type1 = detectMoveType(cards1);
        MoveType type2 = detectMoveType(cards2);
        if (type1 != type2) {
            // Chỉ so sánh khi cùng loại, khác loại thì không hợp lệ (có thể mở rộng cho luật đặc biệt)
            return false;
        }
        // So sánh theo luật từng loại bộ
        switch (type1) {
            case SINGLE:
                return compareSingle(cards1.get(0), cards2.get(0)) > 0;
            case PAIR:
            case TRIPLE:
            case FOUR_OF_A_KIND:
                return compareSameRankSet(cards1, cards2) > 0;
            case SEQUENCE:
                return compareSequence(cards1, cards2) > 0;
            default:
                return false;
        }
    }

    /**
     * So sánh hai lá bài lẻ theo luật Tiến Lên
     */
    public int compareSingle(Card c1, Card c2) {
        int r1 = getTienLenRank(c1);
        int r2 = getTienLenRank(c2);
        if (r1 != r2) return r1 - r2;
        return c1.getSuitOrder() - c2.getSuitOrder();
    }

    /**
     * So sánh hai bộ đôi/ba/tứ quý (giả sử đã hợp lệ: cùng loại, cùng số lượng)
     */
    public int compareSameRankSet(List<Card> set1, List<Card> set2) {
        int max1 = set1.stream().mapToInt(this::getTienLenRank).max().orElse(-1);
        int max2 = set2.stream().mapToInt(this::getTienLenRank).max().orElse(-1);
        if (max1 != max2) return max1 - max2;
        // Nếu bằng nhau, so sánh chất lớn nhất
        int suit1 = set1.stream().filter(c -> getTienLenRank(c) == max1).mapToInt(c -> c.getSuitOrder()).max().orElse(-1);
        int suit2 = set2.stream().filter(c -> getTienLenRank(c) == max2).mapToInt(c -> c.getSuitOrder()).max().orElse(-1);
        return suit1 - suit2;
    }

    /**
     * So sánh hai sảnh (sequence): so sánh lá lớn nhất
     */
    public int compareSequence(List<Card> seq1, List<Card> seq2) {
        int max1 = seq1.stream().mapToInt(c -> getTienLenRank(c)).max().orElse(-1);
        int max2 = seq2.stream().mapToInt(c -> getTienLenRank(c)).max().orElse(-1);
        return max1 - max2;
    }

        /**
     * Kiểm tra bộ bài người chơi chọn có hợp lệ để đánh (so với bàn hiện tại)
     * Nếu currentPile là null hoặc rỗng, chỉ cần hợp lệ theo luật bộ bài
     */
    public boolean isValidMove(List<Card> selectedCards, List<Card> currentPile) {
        MoveType moveType = detectMoveType(selectedCards);
        if (moveType == MoveType.INVALID) return false;
        // Không cho phép đánh đôi/thông chứa 2
        if ((moveType == MoveType.DOUBLE_SEQUENCE || moveType == MoveType.SEQUENCE) && isContainsTwo(selectedCards)) return false;
        if (currentPile == null || currentPile.isEmpty()) return true;
        // Phải cùng loại bộ, hoặc là bộ đặc biệt chặt được (ví dụ tứ quý chặt đôi 2)
        MoveType currentType = detectMoveType(currentPile);
        if (moveType == currentType) {
            return compareHands(selectedCards, currentPile);
        }
        // Luật đặc biệt: tứ quý chặt đôi 2, ba đôi thông chặt đôi 2, bốn đôi thông chặt tứ quý...
        return canBeat(selectedCards, currentPile);
    }

    /**
     * Kiểm tra bộ bài selectedCards có thể chặt được currentPile không (luật đặc biệt)
     */
    public boolean canBeat(List<Card> selectedCards, List<Card> currentPile) {
        MoveType moveType = detectMoveType(selectedCards);
        MoveType currentType = detectMoveType(currentPile);
        // Tứ quý chặt đôi 2
        if (moveType == MoveType.FOUR_OF_A_KIND && currentType == MoveType.PAIR && isContainsTwo(currentPile)) {
            return true;
        }
        // Ba đôi thông chặt đôi 2
        if (moveType == MoveType.DOUBLE_SEQUENCE && selectedCards.size() == 6 && currentType == MoveType.PAIR && isContainsTwo(currentPile)) {
            return true;
        }
        // Bốn đôi thông chặt tứ quý hoặc đôi 2
        if (moveType == MoveType.DOUBLE_SEQUENCE && selectedCards.size() == 8 && (currentType == MoveType.FOUR_OF_A_KIND || (currentType == MoveType.PAIR && isContainsTwo(currentPile)))) {
            return true;
        }
        return false;
    }

    /**
     * Kiểm tra có lá 2 trong bộ bài không (giả sử 2 là 15)
     */
    public boolean isContainsTwo(List<Card> cards) {
        for (Card c : cards) {
            if (c.getValue() == 15) return true; // 2 là 15
        }
        return false;
    }

}
