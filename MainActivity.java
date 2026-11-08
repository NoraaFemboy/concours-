package fr.concoursplus.app;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.text.*;
import org.json.*;
import java.util.*;

public class MainActivity extends Activity {
    final int BG=Color.rgb(15,17,27), PANEL=Color.rgb(25,28,42), CARD=Color.rgb(31,35,52);
    final int WHITE=Color.rgb(245,246,255), MUTED=Color.rgb(158,164,190), PURPLE=Color.rgb(139,124,255), GREEN=Color.rgb(89,211,163);
    LinearLayout root, list; EditText search; String category="Tous"; boolean favoritesOnly=false;
    ArrayList<Contest> contests=new ArrayList<>(); HashSet<String> favorites=new HashSet<>();
    android.content.SharedPreferences prefs;

    static class Contest {
        String title, prize, category, url, end, description;
        Contest(String t,String p,String c,String u,String e,String d){title=t;prize=p;category=c;url=u;end=e;description=d;}
        JSONObject json() throws JSONException { JSONObject o=new JSONObject(); o.put("title",title);o.put("prize",prize);o.put("category",category);o.put("url",url);o.put("end",end);o.put("description",description);return o; }
    }

    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(BG); getWindow().setNavigationBarColor(BG);
        prefs=getSharedPreferences("concoursplus",MODE_PRIVATE); load(); build(); }
    int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(null,Typeface.BOLD);return t;}
    LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
    LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(0);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    void pad(View v,int a,int b,int c,int d){v.setPadding(dp(a),dp(b),dp(c),dp(d));}
    void load(){favorites.addAll(prefs.getStringSet("favorites",new HashSet<>()));String raw=prefs.getString("contests","[]");
        try{JSONArray a=new JSONArray(raw);for(int i=0;i<a.length();i++){JSONObject o=a.getJSONObject(i);contests.add(new Contest(o.optString("title"),o.optString("prize"),o.optString("category"),o.optString("url"),o.optString("end"),o.optString("description")));}}catch(Exception ignored){}}
    void save(){try{JSONArray a=new JSONArray();for(Contest c:contests)a.put(c.json());prefs.edit().putString("contests",a.toString()).putStringSet("favorites",new HashSet<>(favorites)).apply();}catch(Exception ignored){}}
    void build(){
        root=column();root.setBackgroundColor(BG);pad(root,18,18,18,12);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);LinearLayout page=column();
        TextView brand=text("CONCOURS+",13,PURPLE,true);page.addView(brand);
        TextView heading=text("Trouve ton prochain\nsetup à gagner.",29,WHITE,true);LinearLayout.LayoutParams hp=new LinearLayout.LayoutParams(-1,-2);hp.topMargin=dp(9);page.addView(heading,hp);
        TextView sub=text("Les concours gaming et high-tech, réunis au même endroit.",14,MUTED,false);LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,-2);sp.topMargin=dp(7);page.addView(sub,sp);
        LinearLayout actions=row();TextView add=button("+ Ajouter un concours",PURPLE,WHITE);actions.addView(add,new LinearLayout.LayoutParams(0,dp(48),1));add.setOnClickListener(v->addContest());
        TextView info=button("ⓘ",PANEL,WHITE);LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(dp(48),dp(48));ip.leftMargin=dp(8);actions.addView(info,ip);info.setOnClickListener(v->about());LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,-2);ap.topMargin=dp(18);page.addView(actions,ap);
        search=new EditText(this);search.setSingleLine(true);search.setTextColor(WHITE);search.setHintTextColor(MUTED);search.setHint("  Rechercher un PC, une carte graphique…");search.setTextSize(14);search.setBackground(shape(PANEL,14));pad(search,10,2,10,2);LinearLayout.LayoutParams ep=new LinearLayout.LayoutParams(-1,dp(50));ep.topMargin=dp(18);page.addView(search,ep);
        HorizontalScrollView hsv=new HorizontalScrollView(this);hsv.setHorizontalScrollBarEnabled(false);LinearLayout chips=row();String[] cats={"Tous","PC gamer","Composants","Périphériques","Jeux & autres"};for(String c:cats){TextView chip=text(c,13,WHITE,true);pad(chip,15,10,15,10);chip.setBackground(shape(c.equals(category)?PURPLE:PANEL,30));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-2,-2);cp.rightMargin=dp(8);chips.addView(chip,cp);chip.setOnClickListener(v->{category=c;build();});}hsv.addView(chips);LinearLayout.LayoutParams chp=new LinearLayout.LayoutParams(-1,-2);chp.topMargin=dp(14);page.addView(hsv,chp);
        LinearLayout bar=row();TextView title=text("Concours disponibles",19,WHITE,true);bar.addView(title,new LinearLayout.LayoutParams(0,-2,1));TextView fav=text(favoritesOnly?"★ Favoris actifs":"☆ Favoris",13,favoritesOnly?PURPLE:MUTED,true);bar.addView(fav);fav.setOnClickListener(v->{favoritesOnly=!favoritesOnly;build();});LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(-1,-2);bp.topMargin=dp(22);page.addView(bar,bp);
        list=column();LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.topMargin=dp(12);page.addView(list,lp);
        TextView foot=text("Astuce : vérifie toujours les conditions et la date de clôture sur le site organisateur.",12,MUTED,false);LinearLayout.LayoutParams fp=new LinearLayout.LayoutParams(-1,-2);fp.topMargin=dp(20);page.addView(foot,fp);
        scroll.addView(page);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
        search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int count){renderList();}public void afterTextChanged(Editable e){}});
        renderList();
    }
    TextView button(String s,int bg,int fg){TextView t=text(s,14,fg,true);t.setGravity(Gravity.CENTER);t.setBackground(shape(bg,14));return t;}
    void renderList(){if(list==null)return;list.removeAllViews();String q=search==null?"":search.getText().toString().toLowerCase(Locale.ROOT);int count=0;
        for(Contest c:contests){String id=c.title+"|"+c.url;if(!("Tous".equals(category)||c.category.equals(category)))continue;if(favoritesOnly&&!favorites.contains(id))continue;if(!(c.title+" "+c.prize+" "+c.category).toLowerCase(Locale.ROOT).contains(q))continue;count++;
            LinearLayout card=column();card.setBackground(shape(CARD,17));pad(card,16,15,16,15);TextView tag=text(c.category.toUpperCase(Locale.ROOT),10,PURPLE,true);card.addView(tag);
            TextView name=text(c.title,18,WHITE,true);LinearLayout.LayoutParams np=new LinearLayout.LayoutParams(-1,-2);np.topMargin=dp(8);card.addView(name,np);
            TextView prize=text("À gagner : "+c.prize,14,GREEN,true);LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(-1,-2);pp.topMargin=dp(7);card.addView(prize,pp);
            TextView end=text(c.end.isEmpty()?"Date de fin non renseignée":"Fin : "+c.end,12,MUTED,false);LinearLayout.LayoutParams ep=new LinearLayout.LayoutParams(-1,-2);ep.topMargin=dp(7);card.addView(end,ep);
            LinearLayout buttons=row();TextView details=button("Voir le détail",PANEL,WHITE);buttons.addView(details,new LinearLayout.LayoutParams(0,dp(40),1));TextView star=button(favorites.contains(id)?"★":"☆",PANEL,favorites.contains(id)?PURPLE:WHITE);LinearLayout.LayoutParams stp=new LinearLayout.LayoutParams(dp(48),dp(40));stp.leftMargin=dp(8);buttons.addView(star,stp);LinearLayout.LayoutParams btp=new LinearLayout.LayoutParams(-1,-2);btp.topMargin=dp(13);card.addView(buttons,btp);
            details.setOnClickListener(v->details(c));star.setOnClickListener(v->{if(favorites.contains(id))favorites.remove(id);else favorites.add(id);save();renderList();});
            LinearLayout.LayoutParams cardp=new LinearLayout.LayoutParams(-1,-2);cardp.bottomMargin=dp(11);list.addView(card,cardp);
        }
        if(count==0){LinearLayout empty=column();empty.setGravity(Gravity.CENTER);empty.setBackground(shape(PANEL,16));pad(empty,18,25,18,25);empty.addView(text(contests.isEmpty()?"Ton tableau de concours est prêt.":"Aucun résultat pour cette recherche.",16,WHITE,true));TextView hint=text(contests.isEmpty()?"Ajoute un concours avec le bouton ci-dessus. Les concours ne sont pas inventés : tu gardes les liens des organisateurs.":"Essaie une autre recherche ou catégorie.",13,MUTED,false);LinearLayout.LayoutParams h=new LinearLayout.LayoutParams(-1,-2);h.topMargin=dp(8);hint.setGravity(Gravity.CENTER);empty.addView(hint,h);list.addView(empty);}
    }
    void addContest(){LinearLayout form=column();EditText title=field("Nom du concours"),prize=field("Lot à gagner (ex. PC gamer)"),url=field("Lien https://…"),end=field("Date de fin (facultatif)"),desc=field("Description (facultatif)");Spinner cat=new Spinner(this);String[] opts={"PC gamer","Composants","Périphériques","Jeux & autres"};cat.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,opts));form.addView(title);form.addView(prize);form.addView(url);form.addView(end);form.addView(cat);form.addView(desc);
        new AlertDialog.Builder(this).setTitle("Ajouter un concours").setView(form).setNegativeButton("Annuler",null).setPositiveButton("Enregistrer",(d,w)->{String u=url.getText().toString().trim();if(title.getText().toString().trim().isEmpty()||prize.getText().toString().trim().isEmpty()||!u.matches("https?://.+")){new AlertDialog.Builder(this).setMessage("Indique un nom, un lot et un lien valide commençant par https:// ou http://.").setPositiveButton("OK",null).show();return;}contests.add(0,new Contest(title.getText().toString().trim(),prize.getText().toString().trim(),opts[cat.getSelectedItemPosition()],u,end.getText().toString().trim(),desc.getText().toString().trim()));save();build();}).show();}
    EditText field(String hint){EditText e=new EditText(this);e.setHint(hint);e.setTextColor(WHITE);e.setHintTextColor(MUTED);e.setSingleLine(false);e.setTextSize(14);return e;}
    void details(Contest c){new AlertDialog.Builder(this).setTitle(c.title).setMessage("Lot : "+c.prize+"\nCatégorie : "+c.category+"\nFin : "+(c.end.isEmpty()?"Non renseignée":c.end)+"\n\n"+c.description+"\n\nPense à vérifier le règlement et l'éligibilité sur le site officiel.").setNegativeButton("Fermer",null).setPositiveButton("Ouvrir le site", (d,w)->{try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(c.url)));}catch(Exception ignored){}}).show();}
    void about(){new AlertDialog.Builder(this).setTitle("À propos de Concours+").setMessage("Version de test 0.1.0\n\nConcours+ est un carnet de concours gaming/high-tech. Les concours sont ajoutés par l'utilisateur : l'application ne prétend pas vérifier automatiquement leur validité. Vérifie toujours les conditions sur le site organisateur. Les données et favoris restent sur ce téléphone.").setPositiveButton("OK",null).show();}
}
