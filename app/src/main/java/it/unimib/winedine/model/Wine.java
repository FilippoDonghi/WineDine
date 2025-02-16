package it.unimib.winedine.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Wine {

    @PrimaryKey
    @NonNull
    private String id;
    private String name;  //es: merlot
    private String type;  // es. "dry_red_wine"
    private String category;  //es. "red_wine"

    @Ignore
    // Costruttore con type opzionale (predefinito a "unknown" se non passato)
    public Wine(String id, String name, String category) {
        this(id, name, "unknown", category);  // Se type non viene passato, viene impostato su "unknown"
    }

    // Costruttore completo
    public Wine(String id, String name, String type, String category) {
        this.id = id;
        this.name = name;
        this.type = type != null ? type : "unknown";  // Se type è null, impostiamo "unknown"
        this.category = category;
    }

    // Getter e Setter
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getCategory() { return category; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setCategory(String category) { this.category = category; }
}