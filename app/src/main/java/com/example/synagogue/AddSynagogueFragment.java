package com.example.synagogue;

import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.location.*;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;

import com.example.drawerappsyn.R;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import java.io.IOException;
import java.util.*;

public class AddSynagogueFragment extends Fragment implements OnMapReadyCallback {
    EditText name,address,phone,username,password;
    Button register,addHour,addHeader;
    FirebaseAuth auth;
    FirebaseFirestore db;
    GoogleMap map;
    double lat=0,lng=0;
    LinearLayout hours;
    ArrayList<View> rows=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater i,ViewGroup c,Bundle b) {
        return i.inflate(R.layout.fragment_add_synagogue,c,false);
    }

    @Override
    public void onViewCreated(@NonNull View v,@Nullable Bundle b) {
        super.onViewCreated(v,b);

        name=v.findViewById(R.id.editSynagogueName);
        address=v.findViewById(R.id.editAddress);
        phone=v.findViewById(R.id.editPhone);
        username=v.findViewById(R.id.editUsername);
        password=v.findViewById(R.id.editPassword);
        register=v.findViewById(R.id.buttonRegister);
        hours=v.findViewById(R.id.openingHoursContainer);
        addHour=v.findViewById(R.id.buttonAddOpeningHour);
        addHeader=v.findViewById(R.id.buttonAddHeader);

        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();

        SupportMapFragment m=(SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.addSynagogueMap);
        if(m!=null)m.getMapAsync(this);

        address.setOnFocusChangeListener((x,focus)->{if(!focus)searchAddress();});
        addHour.setOnClickListener(x->addHour());
        addHeader.setOnClickListener(x->addHeader());
        register.setOnClickListener(x->register());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap m) {
        map=m;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.7683,35.2137),12));
    }

    void searchAddress() {
        String s=address.getText().toString().trim();
        if(TextUtils.isEmpty(s)||map==null)return;
        try {
            List<Address> a=new Geocoder(requireContext()).getFromLocationName(s,1);
            if(a!=null&&!a.isEmpty()) {
                Address x=a.get(0);
                lat=x.getLatitude(); lng=x.getLongitude();
                LatLng p=new LatLng(lat,lng);
                map.clear();
                map.addMarker(new MarkerOptions().position(p).title("מיקום בית הכנסת"));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(p,16));
            } else toast("לא נמצאה כתובת מתאימה",Toast.LENGTH_SHORT);
        } catch(IOException e) {
            toast("לא ניתן למצוא את הכתובת",Toast.LENGTH_SHORT);
        }
    }

    GradientDrawable bg(int color,int stroke) {
        GradientDrawable g=new GradientDrawable();
        g.setColor(color); g.setCornerRadius(20); g.setStroke(1,stroke);
        return g;
    }

    Button button(String text,int color,float size) {
        Button b=new Button(requireContext());
        b.setText(text); b.setTextSize(size); b.setTextColor(color);
        b.setGravity(Gravity.CENTER); b.setMinWidth(0); b.setMinHeight(0);
        b.setMinimumWidth(0); b.setMinimumHeight(0); b.setPadding(0,0,0,0);
        b.setAllCaps(false); b.setBackgroundColor(Color.TRANSPARENT);
        return b;
    }

    void addHour() {
        LinearLayout row=row();
        LinearLayout moves=moves(row);
        EditText title=edit("כותרת",15,View.TEXT_DIRECTION_FIRST_STRONG);
        EditText content=edit("שעת התחלה",15,View.TEXT_DIRECTION_LTR);
        content.setInputType(android.text.InputType.TYPE_CLASS_DATETIME);
        content.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

        title.setLayoutParams(weight(4));
        content.setLayoutParams(weight(4));

        Button del=button("×",Color.RED,20);
        del.setLayoutParams(new LinearLayout.LayoutParams(42,42));

        del.setOnClickListener(v->remove(row));
        ((Button)moves.getChildAt(0)).setOnClickListener(v->up(row));
        ((Button)moves.getChildAt(1)).setOnClickListener(v->down(row));

        row.addView(moves); row.addView(title); row.addView(content); row.addView(del);
        add(row);
    }

    void addHeader() {
        LinearLayout row=row();
        row.setBackground(bg(Color.rgb(227,242,253),Color.rgb(187,222,251)));

        LinearLayout moves=moves(row);
        int blue=Color.rgb(25,75,120);
        Button up=(Button)moves.getChildAt(0), down=(Button)moves.getChildAt(1);
        up.setTextColor(blue); down.setTextColor(blue);

        EditText text=edit("לדוגמה: תפילות יום חול",17,View.TEXT_DIRECTION_RTL);
        text.setTextColor(blue);
        text.setHintTextColor(Color.rgb(100,140,170));
        text.setTypeface(null,Typeface.BOLD);
        text.setGravity(Gravity.CENTER);
        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        text.setLayoutParams(weight(4));

        Button del=button("×",Color.RED,20);
        del.setLayoutParams(new LinearLayout.LayoutParams(42,42));

        del.setOnClickListener(v->remove(row));
        up.setOnClickListener(v->up(row));
        down.setOnClickListener(v->down(row));

        row.addView(moves); row.addView(text); row.addView(del);
        add(row);
    }

    LinearLayout row() {
        LinearLayout r=new LinearLayout(requireContext());
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        r.setPadding(8,8,8,8);
        r.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        r.setBackground(bg(Color.WHITE,Color.LTGRAY));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,0,0,10); r.setLayoutParams(p);
        return r;
    }


    LinearLayout moves(View row) {
        LinearLayout m = new LinearLayout(requireContext());
        m.setOrientation(LinearLayout.HORIZONTAL);
        m.setGravity(Gravity.CENTER);
        m.setLayoutParams(new LinearLayout.LayoutParams(100, 52));

        Button up = button("▲", Color.DKGRAY, 20);
        Button down = button("▼", Color.DKGRAY, 20);

        up.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
        down.setLayoutParams(new LinearLayout.LayoutParams(48, 48));

        m.addView(up);
        m.addView(down);

        return m;
    }


    EditText edit(String hint,float size,int direction) {
        EditText e=new EditText(requireContext());
        e.setHint(hint); e.setTextSize(size); e.setSingleLine(true);
        e.setTextDirection(direction);
        return e;
    }

    LinearLayout.LayoutParams weight(float w) {
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1);
        p.setMargins(4,0,4,0);
        return p;
    }

    void add(View v) {
        hours.addView(v); rows.add(v); update();
    }

    void remove(View v) {
        hours.removeView(v); rows.remove(v); update();
    }

    void up(View v) {
        int i=hours.indexOfChild(v);
        if(i<=0)return;
        hours.removeView(v); hours.addView(v,i-1);
        rows.remove(v); rows.add(i-1,v); update();
    }

    void down(View v) {
        int i=hours.indexOfChild(v), last=hours.getChildCount()-1;
        if(i<0||i>=last)return;
        hours.removeView(v); hours.addView(v,i+1);
        rows.remove(v); rows.add(i+1,v); update();
    }

    void update() {
        int n=hours.getChildCount();
        for(int i=0;i<n;i++) {
            View v=hours.getChildAt(i);
            if(!(v instanceof LinearLayout))continue;
            View m=((LinearLayout)v).getChildAt(0);
            if(!(m instanceof LinearLayout)||((LinearLayout)m).getChildCount()<2)continue;
            LinearLayout x=(LinearLayout)m;
            x.getChildAt(0).setEnabled(i>0);
            x.getChildAt(1).setEnabled(i<n-1);
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
                m.put("type","header"); m.put("text",text); list.add(m);
            } else if(n==4) {
                String title=((EditText)r.getChildAt(1)).getText().toString().trim();
                String content=((EditText)r.getChildAt(2)).getText().toString().trim();
                if(TextUtils.isEmpty(title)&&TextUtils.isEmpty(content))continue;
                Map<String,String> m=new HashMap<>();
                m.put("type","normal"); m.put("title",title); m.put("content",content);
                list.add(m);
            }
        }
        return list;
    }

    void register() {
        String n=name.getText().toString().trim();
        String a=address.getText().toString().trim();
        String p=phone.getText().toString().trim();
        String u=username.getText().toString().trim();
        String pass=password.getText().toString().trim();

        if(TextUtils.isEmpty(n)){name.setError("יש להזין שם בית כנסת");return;}
        if(TextUtils.isEmpty(a)){address.setError("יש להזין כתובת");return;}
        if(TextUtils.isEmpty(u)){username.setError("יש להזין שם משתמש");return;}
        if(TextUtils.isEmpty(pass)){password.setError("יש להזין סיסמה");return;}
        if(pass.length()<6){password.setError("הסיסמה חייבת להכיל לפחות 6 תווים");return;}

        if(lat==0&&lng==0) {
            searchAddress();
            if(lat==0&&lng==0) {
                toast("יש להזין כתובת שניתן למצוא במפה",Toast.LENGTH_LONG);
                return;
            }
        }

        register.setEnabled(false);
        auth.createUserWithEmailAndPassword(u.toLowerCase()+"@myapp.local",pass)
                .addOnCompleteListener(t->{
                    if(t.isSuccessful()) {
                        FirebaseUser user=auth.getCurrentUser();
                        if(user==null) {
                            register.setEnabled(true);
                            toast("אירעה שגיאה ביצירת המשתמש",Toast.LENGTH_LONG);
                        } else save(user.getUid(),n,a,p,u);
                    } else {
                        register.setEnabled(true);
                        toast("ההרשמה נכשלה: "+(t.getException()!=null?t.getException().getMessage():"שגיאה לא ידועה"),Toast.LENGTH_LONG);
                    }
                });
    }

    void save(String id,String n,String a,String p,String u) {
        Map<String,Object> data=new HashMap<>();
        data.put("name",n); data.put("address",a); data.put("phone",p);
        data.put("username",u); data.put("latitude",lat); data.put("longitude",lng);
        data.put("openingHours",getHours()); data.put("notes","");

        db.collection("synagogues").document(id).set(data)
                .addOnSuccessListener(x->{
                    toast("בית הכנסת נרשם בהצלחה!",Toast.LENGTH_LONG);
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e->{
                    register.setEnabled(true);
                    toast("המשתמש נוצר, אך שמירת הפרטים נכשלה: "+e.getMessage(),Toast.LENGTH_LONG);
                });
    }

    void toast(String text,int length) {
        Toast.makeText(requireContext(),text,length).show();
    }
}
