/*
 * SimpleLemmatizer.java
 * Copyright (C) 2019 Guowei Chen <icgw@outlook.com>
 *
 * Distributed under terms of the GPL license.
 */

package cn.ac.amss.semanticweb.linguistics.lemmatization;

/**
 * Simple lemmatization class
 *
 * @author Guowei Chen (icgw@outlook.com)
 */
public class SimpleLemmatizer
{
  public static enum ENDING {
    ING, EST, ES, S, ED, ER, LY, IES, IED, IER, VED, US, OES, VES, IEST
  }

  public SimpleLemmatizer() {}

  public static String lemma(String stemmed, ENDING ending) {
    if (stemmed == null || stemmed.isEmpty() || stemmed.length() < 2) return "";

    switch (stemmed.charAt(0)) {
      case 'a':
        switch (stemmed.charAt(1)) {
          case 'c':
            switch (stemmed) {
              case "achiev"  : return "achieve";
            }
            break;
          case 'd':
            switch (stemmed) {
              case "advanc"    : return "advance";
              case "adjudicat" : return "adjudicate";
            }
            break;
          case 'f':
            switch (stemmed) {
              case "aft": return "after";
            }
            break;
          case 'm':
            switch (stemmed) {
              case "amb" : return "amber";
              case "amaz": return "amaze";
            }
            break;
          case 'n':
            switch (stemmed) {
              case "analyz"  :
              case "analys"  : return "analyse";
              case "analysi" : return "analyse";
              case "anoth"   : return "another";
              case "answ"    : return "answer";
              case "anu"     : return "anus";
              case "ang"     : return "anger";
            }
            break;
          case 's':
            switch (stemmed) {
              case "associat": return "associate";
            }
            break;
          case 't':
            switch (stemmed) {
              case "attribut": return "attribute";
            }
            break;
        }
        break;
      case 'b':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "bas" : return "base";
              case "bak" : return "bake";
              case "basi": return "basis";
            }
            break;
          case 'e':
            switch (stemmed) {
              case "bel" : return "belly";
              case "bet" : if (ending == ENDING.ER) return "better"; break;
              case "becom": return "become";
            }
            break;
          case 'i':
            switch (stemmed) {
              case "bia": return "bias";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "bor": return "boring";
            }
            break;
          case 'r':
            switch (stemmed) {
              case "broth": if (ending == ENDING.ER) return "brother"; break;
            }
            break;
        }
        break;
      case 'c':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "cas"      : return "case";
              case "calculat" : return "calculate";
              case "caus"     : return "cause";
              case "cak"      : return "cake";
              case "caree"    : return "career";
              case "canc"     : return "cancer";
            }
            break;
          case 'e':
            switch (stemmed) {
              case "centre" : return "center";
              case "cent"   : if (ending == ENDING.ER) return "center"; break;
            }
            break;
          case 'h':
            switch (stemmed) {
              case "chas"    : return "chase";
              case "choos"   : return "choose";
              case "chang"   : return "change";
              case "charg"   : return "charge";
              case "charact" : return "character";
            }
            break;
          case 'l':
            switch (stemmed) {
              case "clas" : return "class";
              case "clos" : return "close";
              case "clon" : return "clone";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "comput"  : if (ending == ENDING.ER) return "computer"; else return "compute";
              case "continu" : return "continue";
              case "cov"     : return "cover";
              case "cours"   : return "course";
              case "consid"  : return "consider";
              case "compar"  : return "compare";
              case "cowork"  : return "coworker";
              case "cod"     : return "code";
              case "complet" : return "complete";
              case "com"     : return "come";
            }
            break;
          case 'r':
            switch (stemmed) {
              case "creat" : return "create";
              case "cros"  : return "cross";
              case "crim"  : return "crime";
            }
            break;
        }
        break;
      case 'd':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "dat"     : return "date";
              case "daught"  : return "daughter";
              case "datacen" : return "datacenter";
            }
            break;
          case 'e':
            switch (stemmed) {
              case "decid"    : return "decide";
              case "decod"    : return "decode";
              case "determin" : return "determine";
              case "declin"   : return "decline";
              case "delet"    : return "delete";
              case "depres"   : return "depress";
            }
            break;
          case 'i':
            switch (stemmed) {
              case "divid"      : return "divide";
              case "discontinu" : return "discontinue";
              case "diagnos"    : return "diagnose";
              case "diseas"     : return "disease";
              case "dif"        : return "differ";
              case "din"        : return "dine";
              case "disclos"    : return "disclose";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "docstr" : return "docstring";
            }
            break;
          case 'r':
            switch (stemmed) {
              case "dri"  : return "dry";
              case "driv" : return "drive";
            }
            break;
        }
        break;
      case 'e':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "eaisy" : return "easy";
            }
            break;
          case 'm':
            switch (stemmed) {
              case "embarras" : return "embarrass";
            }
            break;
          case 'n':
            switch (stemmed) {
              case "encod"   : return "encode";
              case "enginee" : return "engineer";
            }
            break;
          case 'x':
            switch (stemmed) {
              case "explor"    : return "explore";
              case "execut"    : return "execute";
              case "expres"    : return "express";
              case "expir"     : return "expire";
              case "experienc" : return "experience";
              case "exercis"   : return "exercise";
            }
            break;
          case 'v':
            switch (stemmed) {
              case "even" : if (ending == ENDING.ING) return "evening"; break;
            }
            break;
        }
        break;
      case 'f':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "fath"      : return "father";
              case "famy"      : return "family";
              case "facilitat" : return "facilitate";
              case "fac"       : return "face";
            }
            break;
          case 'i':
            switch (stemmed) {
              case "finaliz" : return "finalize";
            }
            break;
          case 'l':
            switch (stemmed) {
              case "flar" : return "flare";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "forc" : return "force";
            }
            break;
          case 'r':
            switch (stemmed) {
              case "freelanc" : return "freelance";
            }
            break;
        }
        break;
      case 'g':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "gam" : return "game";
            }
            break;
          case 'i':
            switch (stemmed) {
              case "giv" : return "give";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "goug"  : return "gouge";
              case "glas"  : return "glass";
              case "googl" : return "google";
            }
            break;
        }
        break;
      case 'h':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "harv"  : return "harvest";
              case "handl" : return "handle";
            }
            break;
          case 'i':
            switch (stemmed) {
              case "hid"  : return "hide";
              case "hik"  : return "hike";
              case "hir"  : return "hire";
              case "hilt" : if (ending == ENDING.ER) return "hitler"; break;
            }
            break;
        }
        break;
      case 'i':
        switch (stemmed.charAt(1)) {
          case 's':
            switch (stemmed) {
              case "issu" : return "issue";
              case "isi"  : return "isis";
            }
            break;
          case 'n':
            switch (stemmed) {
              case "indicat"   : return "indicate";
              case "initializ" : return "initialize";
              case "invit"     : return "invite";
              case "inv"       : return "invest";
              case "inc"       : if (ending == ENDING.EST) return "incest"; break;
              case "intoxicat" : return "intoxicate";
              case "increas"   : return "increase";
              case "introduc"  : return "introduce";
            }
            break;
        }
        break;
      case 'j': break;
      case 'k': break;
      case 'l':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "lat"  : return "later";
              case "larg" : return "large";
              case "las"  : return "laser";
            }
            break;
          case 'e':
            switch (stemmed) {
              case "leas" : return "lease";
            }
            break;
          case 'i':
            switch (stemmed) {
              case "lik" : return "like";
              case "liv" : return "live";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "lov"   : return "love";
              case "locat" : return "locate";
            }
            break;
          case 'u':
            switch (stemmed) {
              case "lucif" : return "lucifer";
            }
            break;
        }
        break;
      case 'm':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "manag"   : return "manage";
              case "maximiz" : return "maximize";
              case "mat"     : if (ending == ENDING.ER) return "matter"; break;
              case "mak"     : return "make";
              case "mast"    : return "master";
            }
            break;
          case 'e':
            switch (stemmed) {
              case "memb"    : return "member";
              case "messeng" : return "messenger";
            }
            break;
          case 'i':
            switch (stemmed) {
              case "minist" : return "minister";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "mov"     : return "move";
              case "moth"    : if (ending == ENDING.ER) return "mother"; break;
              case "movy"    : return "movie";
              case "morn"    : if (ending == ENDING.ING) return "morning"; break;
              case "motivat" : return "motivate";
            }
            break;
        }
        break;
      case 'n':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "nam" : return "name";
            }
            break;
          case 'e':
            switch (stemmed) {
              case "nev" : return "never";
            }
            break;
          case 'i':
            switch (stemmed) {
              case "nic" : return "nice";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "nod" : return "node";
            }
            break;
          case 'u':
            switch (stemmed) {
              case "numb" : return "number";
            }
            break;
         }
         break;
      case 'o':
        switch (stemmed.charAt(1)) {
          case 'b':
            switch (stemmed) {
              case "observ"  : return "observe";
              case "obsolet" : return "obsolete";
              case "obses"   : return "obsess";
            }
            break;
          case 'f':
            switch (stemmed) {
              case "offic"  : if (ending == ENDING.ER) return "officer"; else return "office";
              case "officy" : return "office";
            }
            break;
          case 'p':
            switch (stemmed) {
              case "operat" : return "operate";
            }
            break;
          case 'r':
            switch (stemmed) {
              case "organiz" : if (ending == ENDING.ER) return "organizer"; else return "organize";
              case "organis" : if (ending == ENDING.ER) return "organiser"; else return "organise";
            }
            break;
          case 'u':
            switch (stemmed) {
              case "outcom"   : return "outcome";
              case "outstand" : return "outstanding";
            }
            break;
        }
        break;
      case 'p':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "pag"   : return "page";
              case "pas"   : return "pass";
              case "pap"   : return "paper";
              case "partn" : return "partner";
            }
            break;
          case 'e':
            switch (stemmed) {
              case "peni" : return "penis";
            }
            break;
          case 'h':
            switch (stemmed) {
              case "philosoph" : return "philosopher";
            }
            break;
          case 'i':
            switch (stemmed) {
              case "pierc" : return "pierce";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "powd" : return "powder";
              case "pow"  : if (ending == ENDING.ER) return "power"; break;
              case "pok"  : if (ending == ENDING.ER) return "poker"; else return "poke";
            }
            break;
          case 'r':
            switch (stemmed) {
              case "prioritiz" : return "prioritize";
              case "predicat"  : return "predicate";
              case "pric"      : return "price";
              case "pres"      : return "press";
              case "prepar"    : return "prepare";
              case "provid"    : return "provide";
              case "prot"      : return "protest";
              case "produc"    : return "producer";
              case "propos"    : return "propose";
              case "pref"      : return "prefer";
            }
            break;
          case 'u':
            switch (stemmed) {
              case "purchas" : return "purchased";
            }
            break;
        }
        break;
      case 'q': break;
      case 'r':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "rath"  : return "rather";
            }
            break;
          case 'e':
            switch (stemmed) {
              case "remov"    : return "remove";
              case "recycl"   : return "recycle";
              case "rememb"   : return "remember";
              case "recov"    : return "recover";
              case "remofe"   : return "remove";
              case "regist"   : return "register";
              case "relocat"  : return "relocate";
              case "rearrang" : return "rearrange";
              case "reinv"    : return "reinvest";
              case "reissu"   : return "reissue";
              case "requir"   : return "require";
              case "relat"    : return "relate";
              case "releas"   : return "release";
              case "remaind"  : return "remainder";
            }
            break;
          case 'i':
            switch (stemmed) {
              case "rid" : if (ending == ENDING.ES || ending == ENDING.ING) return "ride"; break;
              case "riv" : return "river";
              case "ris" : if (ending == ENDING.ER)return "riser"; else return "rise";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "rotat": return "rotate";
            }
            break;
        }
        break;
      case 's':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "sampl"   : return "sample";
              case "sav"     : return "save";
              case "saltwat" : return "saltwater";
            }
            break;
          case 'c':
            switch (stemmed) {
              case "sched" : return "schedule";
            }
            break;
          case 'e':
            switch (stemmed) {
              case "sequenc" : return "sequence";
              case "sery"    : return "series";
              case "serv"    : return "server";
            }
            break;
          case 'h':
            switch (stemmed) {
              case "shar" : return "share";
            }
            break;
          case 'i':
            switch (stemmed) {
              case "simp" : return "simple";
              case "sist" : return "sister";
              case "sid"  : if (ending == ENDING.ES || ending == ENDING.ING) return "side"; break;
              case "siz"  : return "size";
            }
            break;
          case 'l':
            switch (stemmed) {
              case "slic" : return "slice";
            }
            break;
          case 'm':
            switch (stemmed) {
              case "smok" : return "smoke";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "soldy" : return "soldier";
            }
            break;
          case 'p':
            switch (stemmed) {
              case "spac"      : return "space";
              case "spee"      : return "speed";
              case "specializ" : return "specialize";
            }
            break;
          case 't':
            switch (stemmed) {
              case "stati"    : return "status";
              case "structur" : return "structure";
              case "stat"     : return "state";
              case "stee"     : return "steer";
              case "starv"    : return "starve";
            }
            break;
          case 'u':
            switch (stemmed) {
              case "substr"      : return "substring";
              case "supercomput" : return "supercomputer";
              case "superpow"    : return "superpower";
              case "surpris"     : return "surprise";
              case "suf"         : return "suffer";
              case "supp"        : return "supply";
              case "sugg"        : return "suggest";
            }
            break;
        }
        break;
      case 't':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "tak" : return "take";
              case "takov" : return "takeover";
            }
            break;
          case 'e':
            switch (stemmed) {
              case "tear"   : if (ending == ENDING.S) return "tears"; break;
              case "tenni"  : return "tennis";
              case "teenag" : return "teenager";
            }
            break;
          case 'h':
            switch (stemmed) {
              case "themselfe" : return "themselves";
              case "thiefe"    : return "thief";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "togeth" : return "together";
            }
            break;
          case 'r':
            switch (stemmed) {
              case "triger" : return "trigger";
              case "tre"    : return "tree";
              case "trad"   : return "trade";
              case "tri"    : return "try";
              case "transf" : return "transfer";
            }
            break;
          case 'w':
            switch (stemmed) {
              case "twit" : return "twitter";
            }
            break;
        }
        break;
      case 'u':
        switch (stemmed.charAt(1)) {
          case 'n':
            switch (stemmed) {
              case "unit"      : if (ending == ENDING.ED) return "united"; break;
              case "und"       : return "under";
              case "undisclos" : return "undisclosed";
            }
            break;
        }
        break;
      case 'v':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "valu"     : return "value";
              case "vandaliz" : return "vandalize";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "vot" : return "vote";
            }
            break;
        }
        break;
      case 'w':
        switch (stemmed.charAt(1)) {
          case 'a':
            switch (stemmed) {
              case "wat"  : return "water";
              case "wast" : return "waste";
            }
            break;
          case 'r':
            switch (stemmed) {
              case "writ" : return "write";
            }
            break;
          case 'o':
            switch (stemmed) {
              case "work" : if (ending == ENDING.ER) return "worker"; break;
              case "wo"   : return "woes";
            }
            break;
        }
        break;
      case 'x': break;
      case 'y': break;
      case 'z': break;
    }

    return stemmed;
  }
}
