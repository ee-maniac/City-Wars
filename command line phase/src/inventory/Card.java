package inventory;

public class Card {
    protected int id;
    protected String name;
    final protected String type;
    final protected String character;
    protected int accuracy;
    protected int damagePerSector;
    protected int duration;
    protected int level;
    protected int upgradeCost;
    protected int cost;

    public Card(String name, String type, String character, int accuracy, int damagePerSector, int duration, int level,
                int upgradeCost, int cost) {
        this.name = name;
        this.type = type;
        this.character = character;
        this.accuracy = accuracy;
        this.damagePerSector = damagePerSector;
        this.duration = duration;
        this.level = level;
        this.upgradeCost = upgradeCost;
        this.cost = cost;
    }

    public Card(int id, String name, String type, String character, int accuracy, int damagePerSector, int duration,
                int level,
                int upgradeCost, int cost) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.character = character;
        this.accuracy = accuracy;
        this.damagePerSector = damagePerSector;
        this.duration = duration;
        this.level = level;
        this.upgradeCost = upgradeCost;
        this.cost = cost;
    }

    public Card(Card card) {
        this.id = card.getId();
        this.type = card.getType();
        this.character = card.getCharacter();
        this.name = card.getName();
        this.accuracy = card.getAccuracy();
        this.damagePerSector = card.getDamagePerSector();
        this.duration = card.getDuration();
        this.level = card.getLevel();
        this.upgradeCost = card.getUpgradeCost();
        this.cost = card.getCost();
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getCharacter() {
        return character;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public int getDamagePerSector() {
        return damagePerSector;
    }

    public int getDuration() {
        return duration;
    }

    public int getLevel() {
        return level;
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }

    public int getCost() {
        return cost;
    }


    public String getImagePath() {
        return "file:src/res/img/cards/" + (this.type.equals("spell") ? this.getName() : "o1") + ".png";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccuracy(int strength) {
        this.accuracy = strength;
    }

    public void setDamagePerSector(int damagePerSector) {
        this.damagePerSector = damagePerSector;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setUpgradeCost(int upgradeCost) {
        this.upgradeCost = upgradeCost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void print() {
        System.out.print("Name: " + name);
        System.out.print(" / Type: " + type);
        System.out.print(" / Attack: " + accuracy);
        System.out.print(" / Damage: " + damagePerSector * duration);
        System.out.print(" / Duration: " + duration);
    }
}