package com.example.synagogue;

import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;

public class EditSynagogueFragment extends Fragment {
    EditText name,address,phone,username;
    Button save,addHour,addHeader;
    FirebaseAuth auth;
    FirebaseFirestore db;
    LinearLayout hours;
    ArrayList<View> rows=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater i,ViewGroup c,Bundle b) {
        return i.inflate(R.layout.fragment_edit_synagogue,c,false);
    }

    @Override
    public void onViewCreated(@NonNull View v,@Nullable Bundle b) {
        super.onViewCreated(v,b);

        name=v.findViewById(R.id.editSynagogueName);
        address=v.findViewById(R.id.editAddress);
        phone=v.findViewById(R.id.editPhone);
        username=v.findViewById(R.id.editUsername);
        hours=v.findViewById(R.id.openingHoursContainer);
        addHour=v.findViewById(R.id.buttonAddOpeningHour);
        addHeader=v.findViewById(R.id.buttonAddHeader);
        save=v.findViewById(R.id.buttonSave);

        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        load();
        addHour.setOnClickListener(x->addHour("",""));
        addHeader.setOnClickListener(x->addHeader(""));
        save.setOnClickListener(x->save());
    }

    void load() {
        if(auth.getCurrentUser()==null) {
            toast("לא נמצא משתמש מחובר",Toast.LENGTH_LONG);
            return;
        }

        db.collection("synagogues").document(auth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(d->{
                    if(!d.exists()) {
                        toast("לא נמצאו פרטי בית הכנסת",Toast.LENGTH_LONG);
                        return;
                    }

                    name.setText(s(d.getString("name")));
                    address.setText(s(d.getString("address")));
                    phone.setText(s(d.getString("phone")));
                    username.setText(s(d.getString("username")));

                    Object data=d.get("openingHours");
                    if(data instanceof ArrayList)
                        for(Object o:(ArrayList<?>)data) {
                            if(!(o instanceof Map))continue;
                            Map<?,?> m=(Map<?,?>)o;
                            String type=s(m.get("type"));

                            if("header".equals(type))
                                addHeader(s(m.get("text")==null?m.get("content"):m.get("text")));
                            else
                                addHour(s(m.get("title")),s(m.get("content")));
                        }
                })
                .addOnFailureListener(e->
                        toast("שגיאה בטעינת הפרטים: "+e.getMessage(),Toast.LENGTH_LONG));
    }

    String s(Object o) {
        return o==null?"":o.toString();
    }

    void addHour(String titleText,String contentText) {
        LinearLayout row=base();
        LinearLayout moves=moves(row);

        EditText title=edit("כותרת",15);
        title.setText(titleText);
        title.setLayoutParams(weight());

        EditText content=edit("שעות פתיחה",15);
        content.setText(contentText);
        content.setLayoutParams(weight());

        Button del=button("×",Color.RED,20);
        del.setLayoutParams(new LinearLayout.LayoutParams(50,55));
        del.setOnClickListener(v->remove(row));

        moves.getChildAt(0).setOnClickListener(v->move(row,-1));
        moves.getChildAt(1).setOnClickListener(v->move(row,1));

        row.addView(moves);
        row.addView(title);
        row.addView(content);
        row.addView(del);
        add(row);
    }

    void addHeader(String text) {
        LinearLayout row=base();
        LinearLayout moves=moves(row);

        EditText header=edit("כותרת / הערה / הפרדה",18);
        header.setText(text);
        header.setTextColor(Color.rgb(40,40,40));
        header.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        header.setGravity(Gravity.CENTER);
        header.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        header.setLayoutParams(weight());

        Button del=button("×",Color.RED,20);
        del.setLayoutParams(new LinearLayout.LayoutParams(50,55));
        del.setOnClickListener(v->remove(row));

        moves.getChildAt(0).setOnClickListener(v->move(row,-1));
        moves.getChildAt(1).setOnClickListener(v->move(row,1));

        row.addView(moves);
        row.addView(header);
        row.addView(del);
        add(row);
    }

    LinearLayout base() {
        LinearLayout r=new LinearLayout(requireContext());
        r.setGravity(Gravity.CENTER_VERTICAL);
        r.setPadding(8,8,8,8);
        r.setBackground(bg());
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,0,0,10);
        r.setLayoutParams(p);
        return r;
    }

    GradientDrawable bg() {
        GradientDrawable g=new GradientDrawable();
        g.setColor(Color.WHITE);
        g.setCornerRadius(20);
        g.setStroke(1,Color.LTGRAY);
        return g;
    }


    LinearLayout moves(View row) {
        LinearLayout m=new LinearLayout(requireContext());
        m.setOrientation(LinearLayout.HORIZONTAL);
        m.setGravity(Gravity.CENTER);
        m.setLayoutParams(new LinearLayout.LayoutParams(110,52));

        Button up=button("▲",Color.DKGRAY,20);
        Button down=button("▼",Color.DKGRAY,20);

        up.setLayoutParams(new LinearLayout.LayoutParams(52,50));
        down.setLayoutParams(new LinearLayout.LayoutParams(52,50));

        m.addView(up);
        m.addView(down);

        return m;
    }


    EditText edit(String hint,float size) {
        EditText e=new EditText(requireContext());
        e.setHint(hint);
        e.setTextSize(size);
        e.setSingleLine(true);
        return e;
    }

    Button button(String text,int color,float size) {
        Button b=new Button(requireContext());
        b.setText(text);
        b.setTextSize(size);
        b.setTextColor(color);
        b.setGravity(Gravity.CENTER);
        b.setPadding(0,0,0,0);
        b.setMinWidth(0);
        b.setMinHeight(0);
        return b;
    }

    LinearLayout.LayoutParams weight() {
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);
        p.setMargins(4,0,4,0);
        return p;
    }

    void add(View row) {
        hours.addView(row);
        rows.add(row);
        update();
    }

    void remove(View row) {
        hours.removeView(row);
        rows.remove(row);
        update();
    }

    void move(View row,int direction) {
        int i=hours.indexOfChild(row), n=i+direction;
        if(i<0||n<0||n>=hours.getChildCount())return;

        hours.removeView(row);
        hours.addView(row,n);
        rows.remove(row);
        rows.add(n,row);
        update();
    }

    void update() {
        int count=hours.getChildCount();
        for(int i=0;i<count;i++) {
            View v=hours.getChildAt(i);
            if(!(v instanceof LinearLayout))continue;

            View x=((LinearLayout)v).getChildAt(0);
            if(!(x instanceof LinearLayout))continue;

            LinearLayout m=(LinearLayout)x;
            if(m.getChildCount()<2)continue;

            m.getChildAt(0).setEnabled(i>0);
            m.getChildAt(1).setEnabled(i<count-1);
        }
    }

    ArrayList<Map<String,String>> getHours() {
        ArrayList<Map<String,String>> list=new ArrayList<>();

        for(int i=0;i<hours.getChildCount();i++) {
            View v=hours.getChildAt(i);
            if(!(v instanceof LinearLayout))continue;

            LinearLayout r=(LinearLayout)v;
            int n=r.getChildCount();

            if(n==3) {
                String text=((EditText)r.getChildAt(1)).getText().toString().trim();
                if(TextUtils.isEmpty(text))continue;

                Map<String,String> m=new HashMap<>();
                m.put("type","header");
                m.put("text",text);
                list.add(m);

            } else if(n==4) {
                String title=((EditText)r.getChildAt(1)).getText().toString().trim();
                String content=((EditText)r.getChildAt(2)).getText().toString().trim();

                if(TextUtils.isEmpty(title)&&TextUtils.isEmpty(content))continue;

                Map<String,String> m=new HashMap<>();
                m.put("type","normal");
                m.put("title",title);
                m.put("content",content);
                list.add(m);
            }
        }
        return list;
    }

    void save() {
        String n=name.getText().toString().trim();
        String a=address.getText().toString().trim();
        String p=phone.getText().toString().trim();
        String u=username.getText().toString().trim();

        if(TextUtils.isEmpty(n)){name.setError("יש להזין שם בית כנסת");return;}
        if(TextUtils.isEmpty(a)){address.setError("יש להזין כתובת");return;}
        if(TextUtils.isEmpty(p)){phone.setError("יש להזין מספר טלפון");return;}
        if(TextUtils.isEmpty(u)){username.setError("יש להזין שם משתמש");return;}

        if(auth.getCurrentUser()==null) {
            toast("לא נמצא משתמש מחובר",Toast.LENGTH_LONG);
            return;
        }

        save.setEnabled(false);

        Map<String,Object> data=new HashMap<>();
        data.put("name",n);
        data.put("address",a);
        data.put("phone",p);
        data.put("username",u);
        data.put("openingHours",getHours());

        db.collection("synagogues")
                .document(auth.getCurrentUser().getUid())
                .update(data)
                .addOnSuccessListener(v->{
                    save.setEnabled(true);
                    toast("הפרטים עודכנו בהצלחה!",Toast.LENGTH_LONG);
                })
                .addOnFailureListener(e->{
                    save.setEnabled(true);
                    toast("שמירת השינויים נכשלה: "+e.getMessage(),Toast.LENGTH_LONG);
                });
    }

    void toast(String text,int length) {
        Toast.makeText(requireContext(),text,length).show();
    }
}
