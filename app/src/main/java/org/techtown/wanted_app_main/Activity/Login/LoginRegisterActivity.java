package org.techtown.wanted_app_main.Activity.Login;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.techtown.wanted_app_main.Activity.MainActivity;
import org.techtown.wanted_app_main.R;
import org.techtown.wanted_app_main.database.Personal;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginRegisterActivity extends AppCompatActivity {

    //성별
    Spinner spinner_gender;
    Integer value_gender;
    //학년
    Spinner spinner_grade;
    Integer value_grade;
    //비밀번호+
    EditText et_pwd, et_pwdcheck;
    TextView pwd_checkmes;
    //아이디
    EditText et_id;
    Button id_dupcheck;
    TextView id_checkmes;
    //이미지
    CheckBox r1, r2, r3, r4, r5, r6;
    String imageName;
    //닉네임
    EditText et_nickname;
    //나이
    EditText et_age;
    //등록
    Button post_person;
    //Dialog
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        //성별,사는곳,학교,학과,학년 spinner 지정
        spinner_gender = findViewById(R.id.register_gender_spinner);
        setSpinner("gender");
        spinner_grade = findViewById(R.id.register_grade_spinner);
        setSpinner("grade");
        //학교 server데이터 가져오기
        //학과 server데이터 가져오기
        //사는곳 server데이터 가져오기


        //id

        et_id=findViewById(R.id.register_id);
        id_checkmes = findViewById(R.id.register_id_check_txt);
        id_dupcheck = findViewById(R.id.register_id_check_btn);
        id_dupcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_id.getText().equals("")){
                    id_checkmes.setText("아이디를 입력해주세요.");
                    return;
                }
                checkId();     //id중복확인
            }
        });


        //비밀번호

        et_pwd = findViewById(R.id.register_pwd);
        et_pwdcheck = findViewById(R.id.register_pwdcheck);
        pwd_checkmes =findViewById(R.id.register_pwdcheck_txt);

        et_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        et_pwdcheck.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        checkPwd();  //비밀번호확인: 비밀번호 재입력창 비밀번호랑 다를경우

        //이미지
        r1 = findViewById(R.id.iv1);
        r2 = findViewById(R.id.iv2);
        r3 = findViewById(R.id.iv3);
        r4 = findViewById(R.id.iv4);
        r5 = findViewById(R.id.iv5);
        r6 = findViewById(R.id.iv6);
        r1.setChecked(true);
        imageName = "profile_basic1";//이미지 기본설정
        selectMyImage(); //이미지 선택


        //닉네임
        et_nickname=findViewById(R.id.register_nickname);

        //동록 버튼 클릭시
        post_person = findViewById(R.id.resup);
        post_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postid = et_id.getText().toString();
                String postpwd = et_pwd.getText().toString();
                String postnickname = et_nickname.getText().toString();
                Integer postgender = value_gender;
                Integer postgrade = value_grade;
                String postimage = imageName; //선택된 이미지url 가져오기
                //Integer postage = Integer.valueOf(et_age.getText().toString());
                //제대로 입력안했을 시
                if ((postid.length() <= 0) && (postpwd.length() <= 0) && (postnickname.length() <= 0) && (et_age.getText().toString().length() <= 0)) {
                    dialog = new Dialog(LoginRegisterActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.register_dialog);
                    dialog.show();
                    Button cancel1 = dialog.findViewById(R.id.btnCancel5);
                    cancel1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                } else {  //제대로 입력했을 시 서버에 post 후 LoginActivity로 이동
                    String url = "http://13.125.214.178:8080/personal";
                    Map map = new HashMap();
                    map.put("stringId", postid);
                    map.put("pwd", postpwd);
                    map.put("nickname", postnickname);
                    map.put("img", postimage);
                    map.put("school", null);
                    map.put("major", null);
                    map.put("grade", postgrade);
                    map.put("age", Integer.valueOf(et_age.getText().toString()));
                    map.put("gender", postgender);
                    map.put("address", null);

                    JSONObject params = new JSONObject(map);

                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject obj) {
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("register_Error", error.getMessage());
                                }
                            }) {

                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=UTF-8";
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(objectRequest);

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });


    }


    public void setSpinner(String topic) { //spinner 설정 함수

        //topic에 따른 spinner설정
        //성별
        if (topic.equals("gender")) {
            ArrayAdapter genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_dropdown_item);
            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_gender.setAdapter(genderAdapter);

            spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        value_gender = 0;
                    } else if (position == 1) {
                        value_gender = 1;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        //학년
        else if (topic.equals("grade")) {
            ArrayAdapter genderAdapter = ArrayAdapter.createFromResource(this, R.array.grade, android.R.layout.simple_spinner_dropdown_item);
            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_grade.setAdapter(genderAdapter);

            spinner_grade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        value_grade = 1;
                    } else if (position == 1) {
                        value_grade = 2;
                    } else if (position == 2) {
                        value_grade = 3;
                    } else if (position == 3) {
                        value_grade = 4;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

    }

    public void checkId() { //아이디 중복검사

        RequestQueue requestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        String url1 = "http://13.125.214.178:8080/personal/stringId/" + et_id.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                id_checkmes.setText("사용 가능한 아이디입니다.");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                id_checkmes.setText("이미 아이디가 존재합니다.");
            }
        });
        requestQueue.add(stringRequest);
    }


    public void checkPwd() { //비밀번호와 비밀번호확인 일치체크
        et_pwdcheck.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_pwdcheck.isFocusable() && s.toString().equals(et_pwd.getText().toString())) {
                    pwd_checkmes.setText("비밀번호를 올바르게 입력했습니다.");

                } else {
                    pwd_checkmes.setText("비밀번호를 다시 확인해주세요.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void selectMyImage() {  //이미지 선택
        r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    r2.setChecked(false);
                    r3.setChecked(false);
                    r4.setChecked(false);
                    r5.setChecked(false);
                    r6.setChecked(false);
                    imageName = "profile_basic1";
                }
            }
        });
        r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    r1.setChecked(false);
                    r3.setChecked(false);
                    r4.setChecked(false);
                    r5.setChecked(false);
                    r6.setChecked(false);
                    imageName = "profile_basic2";
                }
            }
        });
        r3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    r1.setChecked(false);
                    r2.setChecked(false);
                    r4.setChecked(false);
                    r5.setChecked(false);
                    r6.setChecked(false);
                    imageName = "profile_basic3";
                }
            }
        });
        r4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    r1.setChecked(false);
                    r2.setChecked(false);
                    r3.setChecked(false);
                    r5.setChecked(false);
                    r6.setChecked(false);
                    imageName = "profile_basic4";
                }
            }
        });
        r5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    r1.setChecked(false);
                    r2.setChecked(false);
                    r3.setChecked(false);
                    r4.setChecked(false);
                    r6.setChecked(false);
                    imageName = "profile_basic5";
                }
            }
        });
        r6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    r1.setChecked(false);
                    r2.setChecked(false);
                    r3.setChecked(false);
                    r4.setChecked(false);
                    r5.setChecked(false);
                    imageName = "profile_basic6";
                }
            }
        });


    }


}