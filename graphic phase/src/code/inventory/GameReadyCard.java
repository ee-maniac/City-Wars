package code.inventory;

public class GameReadyCard extends Card {
    public GameReadyCard(Card card, int level) {
        super(card.id, card.name, card.type, card.getCharacter(), card.accuracy, card.damagePerSector, card.duration,
                card.level,
                card.upgradeCost,
                card.cost);
        if (!card.type.equals("spell")) {
            int levelDifference = level - this.level;
            for (int i = 0; i < levelDifference; i++) {
                this.accuracy += 5;
                this.damagePerSector += 5;
                this.upgradeCost *= (1 + levelDifference / 4);
            }
            this.level = level;
        }
    }
}