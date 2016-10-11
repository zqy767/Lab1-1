package com.company;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by forandroid on 16-9-23.
 */
public class Union implements Cloneable{
    ArrayList<Poly> ans;

    Union () {
        ans = new ArrayList<Poly>();
    }

    public Object clone () throws CloneNotSupportedException{
        Union cloned = new Union();
        for (Poly p:ans) {
            cloned.ans.add((Poly)p.clone());
        }

        return cloned;
    }

    private char n_string  (String s,int n){
        return s.charAt(n);
    }

    private char first_string (String s) {
        return n_string(s, 0);
    }

    private class morpheme_obj {
        String morpheme;
        int flag;

        morpheme_obj (String morpheme,int flag) {
            this.flag = flag;
            this.morpheme = morpheme;
        }
    }

    private ArrayList<morpheme_obj> split_morpheme (String string) throws Exception {
        int num = 0,start_point = 0,flag;
        if (first_string(string) == '-') {start_point++; flag = -1;}
        else flag = 1;

        ArrayList<morpheme_obj> ans = new ArrayList<morpheme_obj>();
        for (int i = start_point; i < string.length(); i++) {
            if (n_string(string,i) == '(') num++;
            else if (n_string(string,i) == ')') num--;
            else if (n_string(string,i) == '+' && num == 0) {
                ans.add(new morpheme_obj(string.substring(start_point,i),flag));
                flag = 1;
                start_point = i+1;
            }

            else if (n_string(string,i) == '-' && num == 0 && n_string(string,i-1) != '*') {
                ans.add(new morpheme_obj(string.substring(start_point,i),flag));
                flag = -1;
                start_point = i+1;
            }
        }

        ans.add(new morpheme_obj(string.substring(start_point,string.length()),flag));
        if (num != 0) throw new Exception();
        return ans;
    }

    private Poly equals (Poly p) {
        for (Poly pre: ans) {
            if (pre.it.size() != p.it.size()) continue;
            int flag = 0;
            for (int i = 0; i < p.it.size(); i++) {
                if (!pre.it.get(i).name.equals(p.it.get(i).name) ||
                        pre.it.get(i).power != p.it.get(i).power) {
                    flag = 1;
                    break;
                }
            }

            if (flag == 0) return pre;
        }

        return null;
    }

    // this method will DESTORY our ans

    private void merge_same_item (Poly tmp) {
        Poly k = equals(tmp);
        if (k != null) {
            k.prenum += tmp.prenum;
            if (k.prenum == 0) ans.remove(k);
        }
        else ans.add(tmp);
    }

    public void turn_string_to_list (String string) throws Exception{
        for (morpheme_obj i: split_morpheme(string)) {
            Poly tmp = new Poly();
            tmp.morpheme(i.morpheme,i.flag);
            merge_same_item(tmp);
        }

    }

    public String list_to_string () {
        if (ans.isEmpty() || ans.size() == 1 && ans.get(0).prenum == 0) return "0";
        String string = "";
        for (Poly i: ans) {
            int prenum = i.prenum;
            if (prenum != 0) {
                if (prenum < 0)
                    string = string.concat(String.valueOf(prenum));
                else
                    if (string.equals(""))
                        string = String.valueOf(prenum);
                    else
                        string = string.concat("+").concat(String.valueOf(prenum));

                for (Item var : i.it) {
                    if (var.power == 1)
                        string = string.concat("*").concat(var.name);
                    else
                        string = string.concat("*").concat(var.name)
                                .concat("^").concat(String.valueOf(var.power));
                }
            }
        }
        return string;
    }


    private Poly symbol_to_number (Poly p,ArrayList<Var> vars) throws Exception {
        Poly ans = new Poly();
        ans.prenum = p.prenum;

        for (Item i:p.it) {
            if (first_string(i.name) == '(') {
                Union bracket = new Union();
                bracket.turn_string_to_list(i.name.substring(1,i.name.length() -1));
                String bracket_string = bracket.simply(vars);
                try {
                    int lpre = Integer.parseInt(bracket_string);
                    ans.prenum *= Math.pow(lpre,i.power);
                    if (p.prenum == 0) return null;
                } catch (NumberFormatException e) {
                    bracket_string = "(".concat(bracket_string).concat(")");
                    ans.it.add(new Item(bracket_string,i.power));
                }

            }
            else {
                int flag = 0;
                for (Var v: vars) {
                    if (v.name.equals(i.name)) {
                        flag = 1;
                        ans.prenum *= Math.pow(v.value, i.power);
                        if (p.prenum == 0) return null;
                    }
                }
                if (flag == 0) ans.it.add(i);
            }
        }
        return ans;
    }

    public String simply (ArrayList<Var> vars) throws Exception{
        ArrayList<Poly> tmp = new ArrayList<Poly>();
        tmp.addAll(ans);
        ans.clear();

        for (Poly i: tmp) {
            Poly new_poly = symbol_to_number(i,vars);
            if (new_poly != null)
                merge_same_item(new_poly);
        }

        return list_to_string();
    }

    private int power_1_ot_not (Poly p,Item it) {
        if (it.power == 1) {
            p.remove(it);
            return 1;
        }
        int k = it.power;
        it.power--;
        return k;
    }


    private Poly derivative_single (Poly p,Item it,String sym) throws Exception{
        Poly ans = new Poly();
        ans.prenum = p.prenum;
        ans.it.addAll(p.it);

        if (first_string(it.name) != '(') {
            if (it.name.equals(sym)) {
                ans.prenum *= power_1_ot_not(ans,it);
                if (ans.prenum == 0) return null;
            } else return null;
        }
        else {
            Union bracket = new Union();
            bracket.turn_string_to_list(it.name.substring(1,(it.name.length()-1)));
            String bracket_string = bracket.derivative(sym);
            try {
                int pre = Integer.parseInt(bracket_string);
                ans.prenum *= Math.pow(pre,it.power);
                ans.prenum *= power_1_ot_not(ans,it);
                if (ans.prenum == 0) return null;
            }catch (NumberFormatException e) {
                bracket_string = "(".concat(bracket_string).concat(")");
                Item new_item = new Item(bracket_string,1);
                ans.it.add(new_item);
                ans.prenum *= power_1_ot_not(ans,it);
                if (ans.prenum == 0) return null;
            }
        }
        return ans;
    }


    private ArrayList<Poly> derivative_to_poly (Poly p,String sym) throws Exception {
        ArrayList<Poly> ans = new ArrayList<>();
        for (Item it:p.it) {
            Poly tmp = derivative_single(p,it,sym);
            if (tmp != null)
                ans.add(tmp);
        }
        return ans;
    }

    public String derivative (String sym) throws Exception{
        ArrayList<Poly> tmp = new ArrayList<Poly>();
        tmp.addAll(ans);
        ans.clear();

        for (Poly i: tmp) {
            ArrayList<Poly> new_poly = derivative_to_poly(i,sym);
            for (Poly p: new_poly)
                merge_same_item(p);

        }

        return list_to_string();

    }

}
//4//2