package com.example.bullbearwar.Searching;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bullbearwar.MainActivity;
import com.example.bullbearwar.R;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

/**
 * Created by User on 24 Feb 2018.
 */

public class YahooSearchActivity extends Fragment {

    TextView mOutputText;
    AutoCompleteTextView autoCompleteTextView;
    StockHintAdapter stockHintAdapter;

    TextView QuoteNum, QuoteName, LastUpdatedTime;
    TextView PreviousCloseNum, OpenNum, HighNum, LowNum, VolumnNum;
    TextView BidNum, AskNum, DayRangeNum, WeekRangeNum, AvgVolumnNum;
    TextView MarketCapNum, BetaNum, PERationNum, EpsNum, EraningDateNum;
    CandleStickChart mChart;
    TextView currentPrice, currentPriceChange;
    Spinner marketSpinner;


    //    final String[] stockHints = {"0005.HK", "HSBC company Name", "匯豐",
//            "3988.HK", "bank company Name", "中銀", "0001.HK", "0059.HK"};
    public static final String[] stockNameEnAll = {"C AUTO INT DECO", "ASCENT INT'L", "PRIMEVIEW HLDG", "ELEGANCEOPTICAL", "SITOY GROUP", "GUANGDONG TANN", "MAINLAND HOLD", "ARTS OPTICAL", "UKF", "BEST FOOD HLDG", "SAMSONITE", "KEE", "WAHSUN HANDBAGS", "WINOX", "CLEAR MEDIA", "SEEC MEDIA", "KK CULTURE", "SINOMEDIA", "CN CULTURE GP", "BRANDINGCHINA", "BEIJING MEDIA", "SHIFANG HLDG", "ASIARAY", "CHINA 33MEDIA", "OOH HOLDINGS", "CORNERSTONE FIN", "GURU ONLINE", "INNO-TECH HOLD", "WINTO GROUP", "DAHE MEDIA", "HYPEBEAST", "C.P. POKPHAND", "ASIAN CITRUS", "WH GROUP", "LAM SOON (HK)", "CHINA AGRI", "WAI CHUN MINING", "GOLDEN RES DEV", "CHAODA MODERN", "CHINA FIN INV", "HUA LIEN INT'L", "CHANGSHOUHUA", "PACIFIC ANDES", "HUISHENG INTL", "COFCO MEAT", "PUTIAN FOOD", "CHINA STARCH", "DACHAN FOOD", "CH GREENFRESH", "GOAL FORWARD", "WEALTH GLORY", "HAECO", "CATHAY PAC AIR", "CHINA EAST AIR", "AIR CHINA", "CHINA SOUTH AIR", "CALC", "BOC AVIATION", "HNA INFRA", "BEIJING AIRPORT", "TSINGTAO BREW", "SAN MIGUEL HK", "CHINA RES BEER", "OCI INTL", "TONTINE WINES", "NEW SILKROAD", "DYNASTY WINES", "SILVER BASE", "MAJOR HLDGS", "MADISON HLDG", "C FORTUNE INV", "WINE'S LINK", "CONCORD NE", "KONG SUN HOLD", "GCL NEWENERGY", "RUIFENG RENEW", "CNE TECH CORP", "PANDA GREEN", "CP CLEAN ENERGY", "HN RENEWABLES", "CH RENEW EN INV", "BE CLEAN ENERGY", "LT SMART ENERGY", "DATANG RENEW", "XINTE ENERGY", "C G SOURCE ENGY", "NORTH NEWENERGY", "TK NEW ENERGY", "FE HLDGS INTL", "DICKSON CONCEPT", "CROCODILE", "ENM HOLDINGS", "MOISELLE INT'L", "ESPRIT HOLDINGS", "TOP FORM INT'L", "YGM TRADING", "GLORIOUS SUN", "BAUHAUS INT'L", "GOLDLION HOLD", "PORTICO INT'L", "BOSSINI INT'L", "FULLSUN INT'L", "JOYCE BOUTIQUE", "GIORDANO INT'L", "GLOBAL BRANDS", "TRINITY", "DAOHE GLOBAL", "STATE EN ASSETS", "THEME INT'L", "I.T", "CH OUTFITTERS", "MILAN STATION", "CHINA LILANG", "MIKO INTL", "NUOQI", "361 DEGREES", "VESTATE GROUP", "CEFC FIN INV", "NEW FOCUS AUTO", "AUTO ITALIA", "TUS INTL", "ZHONGSHENG HLDG", "SPARKLE ROLL", "VICTORY GROUP", "MEIDONG AUTO", "FIRST CAP GP", "GRAND BAOXIN", "RUNDONG AUTO", "ZHENGTONGAUTO", "SUNFONDA GP", "DCH HOLDINGS", "YONGDA AUTO", "HARMONY AUTO", "G. A. HOLDINGS", "ZMFY GLASS", "ZHENG LI HLDG", "XINYI HK", "GEELY AUTO", "JOHNSON ELEC H", "WULING MOTORS", "VMEP HOLDINGS", "MINTH GROUP", "CH DYNAMICS", "DONGFENG GROUP", "TAN CHONG INT'L", "FDG EVEHICLES", "TIANNENG POWER", "ZHONGDA INT'L", "CHAOWEI POWER", "COSLIGHT TECH", "ZHEJIANG SHIBAO", "BRILLIANCE CHI", "CHINA ENV RES", "POWER XINCHEN", "HAN TANG INTL", "HYBRID KINETIC", "REALORD GROUP", "BYD COMPANY", "SHUANGHUA H", "NEXTEER", "BISU TECH", "XIN POINT HOLD", "YADEA", "BAIC MOTOR", "RUIFENG POWER", "GAC GROUP", "GREATWALL MOTOR", "HSBC HOLDINGS", "HANG SENG BANK", "BANK OF E ASIA", "BANKOFJINZHOU", "DAH SING", "PUBLIC FIN HOLD", "CCB", "CITIC BANK", "CHONG HING BANK", "ZYBANK", "ABC", "ICBC", "GRCB", "BANK OF TIANJIN", "PSBC", "BCQ", "MINSHENG BANK", "CZBANK", "SHENGJINGBANK", "BANK OF GANSU", "DAHSING BANKING", "BOC HONG KONG", "STANCHART", "BANKCOMM", "CQRC BANK", "HUISHANG BANK", "BQD", "CM BANK", "BANK OF CHINA", "BOCOM 15USDPREF", "INNOVATIVE PHAR", "UNI-BIO GROUP", "BBI LIFE SCI", "FUDANZHANGJIANG", "3SBIO", "GENSCRIPT BIO", "WUXI BIO", "MEILLEUREHEALTH", "HAOHAI BIOTEC", "HK LIFE SCI", "CRMI", "BIOSINO BIO-TEC", "LUMENA NEWMAT", "DONGYUE GROUP", "C ZENITH CHEM", "YIP'S CHEMICAL", "TIANDE CHEMICAL", "INFINITY DEV", "CNT GROUP", "L & M CHEMICAL", "GLOBAL BIO-CHEM", "CHANGMAO BIO", "NGAI HING HONG", "DENOX ENV", "MANFIELD CHEM", "TIANHE CHEM", "CHI LONGEVITY", "TSAKER CHEM", "FIRST CHEM", "CHINA SANJIANG", "ECOGREEN", "CHINA FLAVORS", "CT ENTERPRISE", "CHANGAN RH", "NW INDUSTRY", "GREEN LEADER", "GRAND OCEAN AR", "NUR HOLDINGS", "MONGOLIA ENERGY", "UP ENERGY DEV", "ROSAN RES", "SHOUGANG RES", "ARES ASIA", "HUSCOKE RES", "BEL GLOBAL RES", "ASIA COAL", "CHINA QINFA", "MONGOL MINING", "LOUDONG GN RES", "CHINA SHENHUA", "AGRITRADE RES", "SIBERIAN MINING", "YANZHOU COAL", "SUPERB SUMMIT", "NAN NAN RES", "KINETIC M&E", "HIDILI INDUSTRY", "CH UNIENERGY", "E-COMMODITIES", "FS ANTHRACITE", "SOUTHGOBI-S", "CHINA COAL", "YITAI COAL", "JINMA ENERGY", "KAISUN ENERGY", "COMEC", "CHI OCEAN IND", "SINGAMAS CONT", "HUARONG ENERGY", "QINGLING MOTORS", "CRRC", "SINOTRUK", "CRRC TIMES ELEC", "CKH HOLDINGS", "WHARF HOLDINGS", "SWIRE PACIFIC A", "BAN LOONG HOLD", "HOPEWELL HOLD", "SOUTH SEA PETRO", "SWIRE PACIFIC B", "CARRIANNA", "EVERCHINA INT'L", "MIN XIN HOLD", "SHUN TAK HOLD", "SE ASIA PPT", "CITIC", "SHANGHAI IND H", "FOSUN INTL", "NWS HOLDINGS", "CHINA TANGSHANG", "ZHUHAI H INV", "CKI HOLDINGS", "CELESTIAL ASIA", "LERADO FIN", "LEGENDHOLDING", "CH DEMETER FIN", "VANTAGE INT'L", "CHEVALIER INT'L", "CHINA SAITE", "FSE ENGINEERING", "CHINNEY ALLI", "HSIN CHONG GP", "YAU LEE HOLD", "AMCO UNITED", "HKICIM GROUP", "ASIA ALLIED INF", "SINGYES SOLAR", "LING YUI", "FAR EAST GLOBAL", "HANISON", "SOCAM DEV", "WIN WIN WAY", "MECOM POWER", "CNQC INT'L", "BOILLHEALTHCARE", "GREENLAND BROAD", "GRAND MING", "VISION FAME", "CHUAN HOLDING", "SFK", "JUJIANG CONS", "GOLD-FIN HLDG", "C CHENG HLDGS", "JIYIHOUSEHOLD", "LEAP HLDGS GP", "INCON", "SUNWAY INT'L", "LUKS GROUP (VN)", "SOFTPOWER INT", "SHANSHUI CEMENT", "DONGWU CEMENT", "WORLD HOUSEWARE", "ASIA CEMENT CH", "JUNEFIELD GROUP", "CONCH CEMENT", "HK SH ALLIANCE", "CHINA TIANRUI", "TONGFANG KONTA", "CHINARES CEMENT", "SANROC INT'L", "SINOMA", "BBMG", "CHINA LESSO", "WESTCHINACEMENT", "CNBM", "ROYAL DELUXE", "TWINTEK", "STEEDORIENTAL", "SK TARGET", "GOLD PEAK", "CHINA FORTUNE", "IDT INT'L", "GRANDE HOLDINGS", "NATIONAL ELEC H", "HKC INT'L HOLD", "BYD ELECTRONIC", "VTECH HOLDINGS", "COMPUTIME", "ALCO HOLDINGS", "CHINA BEST", "CHINASINOSTAR", "GROUP SENSE", "ALLTRONICS", "SUGA INT'L", "FUJIKON IND", "CHINA-HK PHOTO", "CH HCARE ENT", "TONLY ELEC", "SIS MOBILE", "SCUD GROUP", "COWELL", "HONGDA FIN", "TEN PAO GROUP", "FIH", "SMIT", "COOLPAD GROUP", "SKYLIGHT HLDG", "VITAL MOBILE", "MILLENNIUM PG", "NATURAL BEAUTY", "SA SA INT'L", "BONJOUR HOLD", "MODERN BEAUTY", "L'OCCITANE", "HENGAN INT'L", "WATER OASIS GP", "VEEKO INT'L", "CH CHILD CARE", "BAWANG GROUP", "PERFECT SHAPE", "EVERGREEN PG", "GREEN INTL HLDG", "VINDA INT'L", "HINSANG GROUP", "SUPERROBOTICS", "SAU SAN TONG", "CH GOLD CLASSIC", "MIRICOR", "TAKBO GROUP", "MI MING MART", "NATURAL DAIRY", "DAQING DAIRY", "H&H INTL HLDG", "CH MODERN D", "YASHILI INT'L", "YST DAIRY", "CHINA SHENGMU", "ZHONGDI DAIRY", "ZHUANGYUAN PA", "AUSNUTRIA", "MENGNIU DAIRY", "HUISHAN DAIRY", "CH BEIDAHUANG", "HENDERSON INV", "C.P. LOTUS", "CENTURY GINWA", "SINCERE", "WING ON CO", "SHIRBLE STORE", "GOME RETAIL", "JIAHUA STORES H", "CEC INT'L HOLD", "JINGKELONG", "NWDS CHINA", "CR ASIA", "MAOYE INT'L", "SKL", "LIANHUA", "AEON STORES", "LIFESTYLE INT'L", "SPRINGLAND", "LIFESTYLE CHI", "YI HUA HLDG", "GOLDEN EAGLE", "PARKSON GROUP", "SUNART RETAIL", "WANT WANT CHINA", "U-PRESID CHINA", "VITASOY INT'L", "FOUR SEAS MER", "CHINA FOODS", "TENWOW INT'L", "MIRAMAR HOTEL", "JINJIANG HOTELS", "SINO-I TECH", "PAX GLOBAL", "SMARTAC GP CH", "COGOBUY", "BOYAA", "FORGAME", "TRADELINK", "PACIFIC ONLINE", "FUTURE WORLD FH", "TENCENT", "CHINA LIT", "NETDRAGON", "IGG", "CHINA E-WALLET", "FEIYU", "FORTUNET E-COMM", "LEYOU TECH H", "C P PROCUREMENT", "RAZER", "MEITU", "TIANGE", "DIGIT HOLLYWOOD", "BAIOO", "HC INTL", "IBO TECH", "VOBILE GROUP", "OURGAME", "GLOBALSTRAT", "EFT SOLUTIONS", "SINO VISION WW", "NE ELECTRIC", "SUN.KING ELEC", "C TRANSMISSION", "PERENNIAL INT'L", "DONGFANG ELEC", "HARBIN ELECTRIC", "CHINA ENERGINE", "GREENS HOLDINGS", "JIANGNAN GP", "BOER POWER", "TITANS ENERGY", "GOLDWIND", "SH ELECTRIC", "CH ENERGY ENG", "CLP HOLDINGS", "POWER ASSETS", "AMBER ENERGY", "JNCEC", "HUADIAN FUXIN", "CHINA RES POWER", "TIANJIN DEV", "HUANENG POWER", "CHINA LONGYUAN", "DATANG POWER", "HUADIAN POWER", "CANVEST ENV", "VPOWER GROUP", "IMEEI", "HAITIAN ENERGY", "CGN NEW ENERGY", "CGN POWER", "CHINA POWER", "HKELECTRIC-SS", "TIANLI HOLDINGS", "CAPXON INT'L", "WILLAS-ARRAY", "MEGA MEDICAL", "DATRONIX HOLD", "MAN YUE TECH", "S.A.S. DRAGON", "MOBICON GROUP", "FOREBASE INTL", "HI-LEVEL TECH", "TEM HOLDINGS", "CHINA SOLAR", "IRICO NEWENERGY", "HANERGY TFP", "COMTEC SOLAR", "SOLARGIGA", "CASSAVA RES", "ENVIRO ENERGY", "SFCE", "TRONY SOLAR", "GCL-POLY ENERGY", "SINGYES NM", "SINOFERT", "SUNSHINE", "KO YO GROUP", "CHINA AGROTECH", "DONGGUANG CHEM", "CHINA XLX FERT", "CHINA BLUECHEM", "TEDA BIOMEDICAL", "HUABAO INTL", "FUFENG GROUP", "YIHAI INTL", "S&P INTL HLDG", "HONWORLD GP", "VEDAN INT'L", "GLOBAL SWEET", "DAPHNE INT'L", "YUE YUEN IND", "PEGASUS INT'L", "LE SAUNDA", "SHAW BROTHERS", "C.BANNER", "SINO ENERGY INT", "BAOFENGMODERN", "KINGMAKER", "SYMPHONY HOLD", "S. CULTURE", "XTEP INT'L", "FUGUINIAO", "STELLA HOLDINGS", "ANTA SPORTS", "LI NING", "EVERSMART INT", "GREENHEART GP", "SUSTAIN FOREST", "CA LOW-CARBON", "DA SEN HLDGS", "CH WOOD OPT", "GREAT WORLD", "IR RESOURCES", "HONG WEI ASIA", "ANXIANYUAN CH", "FU SHOU YUAN", "SAGE INT'L GP", "GRAND PEACE", "C WAN TONG YUAN", "SINO-LIFE GROUP", "HING LEE (HK)", "KENFORD GROUP", "KASEN", "LISI GP HOLD", "SAMSON HOLDING", "E. BON HOLDINGS", "TECHTRONIC IND", "CHINA HOUSEHOLD", "IMPERIUM GP", "CARPENTER TAN", "CHINA JICHENG", "GOODBABY INTL", "BOLINA", "ROYALE FURN", "CH ENV TECH&BIO", "NEWTREE GROUP", "IH RETAIL", "SINOMAX GROUP", "SYNERGY GROUP", "MORRIS HOLD", "CREATIVE GLOBAL", "ULFERTS", "NEO-NEON", "MAN WAH HLDGS", "OZNER WATER", "CHINA LUDAO", "NATURE HOME", "NVC LIGHTING", "CH BAOFENG INTL", "KING'S FLAIR", "GALAXY ENT", "RICH GOLDMAN", "SUMMIT ASCENT", "MELCO INT'L DEV", "EMPEROR E HOTEL", "CHINA STAR ENT", "REXLOT HOLDINGS", "LANDING INTL", "SJM HOLDINGS", "AMAX INT HOLD", "IMPERIAL PAC", "WYNN MACAU", "STARLIGHT CUL", "PARADISE ENT", "SUCCESS DRAGON", "NIRAKU", "CHINA LOTSYN", "OKURA HOLDINGS", "MACAU LEGEND", "SANDS CHINA LTD", "MGM CHINA", "NAGACORP", "DYNAM JAPAN", "CHINA VANGUARD", "LOTO INTERACT", "AGTECH HOLDINGS", "ASIA PIONEER", "HK & CHINA GAS", "CHINA GAS HOLD", "BEIJING ENT", "CHINA INFRA INV", "CHINA OIL & GAS", "CHI PEOPLE HOLD", "CHINA SUNTIEN", "TOWNGAS CHINA", "CHINA RES GAS", "TIANJINJINRAN", "SUCHUANG GAS", "TIAN LUN GAS", "DZUG", "ENN ENERGY", "BINHAI INV", "ZHONGYU GAS", "BG BLUE SKY", "CHI P ENERGY", "SUN HING VISION", "CKLIFE SCIENCES", "WAI YUEN TONG", "QIANHAI HEALTH", "BESUNYEN", "SHUNTEN INTL", "EFORCE HOLDINGS", "HUIYIN HLDGS GP", "REAL NUTRI", "AUSUPREME", "SINOLIFE UTD", "TOWN HEALTH", "MEXAN", "FE HOTELS", "HK&S HOTELS", "SHANGRI-LA ASIA", "REGAL INT'L", "CHINA INV HOLD", "FUJIAN HOLDINGS", "KECK SENG INV", "CAPITAL ESTATE", "MAGNIFICENT", "SHUNHO PROPERTY", "SHUNHO HOLDINGS", "ASIA STD HOTEL", "CENTURY C INT'L", "CENERIC", "TIANYUAN HEALTH", "PALIBURG HOLD", "CROWNICORP", "ROSEDALE HOTEL", "KAI YUAN HLDGS", "SINO HOTELS", "LANGHAM-SS", "LEGEND STRAT", "JINMAO HOTEL-SS", "LINK HOLDINGS", "RAYMOND IND", "CHIGO HOLDING", "KIN YAT HOLD", "ALLAN INT'L", "SKYWORTHDIGITAL", "HISENSE KELON", "TCL MULTIMEDIA", "HAIER ELEC", "HUIYIN SMARTCOM", "JIU RONG HOLD", "IMS GROUP", "GLOBAL ENERGY", "E LIGHTING", "CHINA AEROSPACE", "YUSEI", "HENGTEN NET", "KINGBOARD CHEM", "LUNG KEE", "CHINA FIRE", "TC ORI LIGHT", "WKK INTL (HOLD)", "DAISHOMICROLINE", "K & P INT'L", "KA SHUI INT'L", "EVA HOLDINGS", "LEOCH INT'L", "XINYI GLASS", "IPE GROUP", "XINYI SOLAR", "V.S. INT'L", "LUOYANG GLASS", "ELEC & ELTEK", "SOLARTECH INT'L", "CHINA HENGSHI", "YAN TAT GROUP", "YICHEN IND", "PANTRONICS HLDG", "AKM INDUSTRIAL", "ISDN HOLDINGS", "TRIO IND ELEC", "KB LAMINATES", "XINGDA INT'L", "CPM GROUP", "BUILD KING HOLD", "CHINA RAILWAY", "WAI KEE HOLD", "SHENYANG PUBLIC", "CH DREDG ENV", "CHINA RAIL CONS", "KINGBO STRIKE", "CHINNEY KW", "KWAN ON HLDGS", "UCD", "CHINA COMM CONS", "CMEC", "MAN KING HOLD", "PROSPER CONS", "GREAT WATER", "CNC HOLDINGS", "ZJ UNITED INV", "ASIA FINANCIAL", "MANULIFE-S", "CHINA TAIPING", "AIA", "NCI", "PICC GROUP", "CHINA RE", "PING AN", "PICC P&C", "PRU", "CPIC", "CHINA LIFE", "ZA ONLINE", "TARGET INS", "ASIA INV FIN", "LI & FUNG", "EMINENCE ENT", "OCEAN ONE HLDG", "GUOCO GROUP", "LEE HING", "CNEWECON FUND", "CHINA MERCHANTS", "CCT FORTIS", "CHINA EB LTD", "GEMINI INV", "CHINA INV DEV", "MASON GP HOLD", "PROSP INV HOLD", "EARNEST INV", "DT CAPITAL", "PT INTL DEV", "FDG KINETIC", "HUGE CHINA", "ORIENTAL EXPL", "CHINA INV FUND", "YUGANG INT'L", "SOUTH CHINA FIN", "SHK HK IND", "TAI UNITED HOLD", "C FIN INT INV", "UBA INVESTMENTS", "SHANGHAI GROWTH", "VALUE PARTNERS", "CH INTERNET INV", "SHENG YUAN HLDG", "EAGLERIDE INV", "GLOBAL M CAP", "UNITY INV HOLD", "WONG'S INT'L", "GUOAN INTL", "SUPERACTIVE GP", "YEEBO (INT'L H)", "CCT LAND", "CH DISPLAY OPT", "FUTONG TECH", "CIL HOLDINGS", "SANDMARTIN INTL", "SIS INT'L", "NANJING PANDA", "TONGDA GROUP", "BOE VARITRONIX", "TRULY INT'L", "VSTECS", "TPV TECHNOLOGY", "MOBI DEV", "Z-OBEE", "LENOVO GROUP", "KARRIE INT'L", "PINE TECH", "INVESTECH HLDGS", "PC PARTNER", "CHINA GOLDJOY", "WAI CHI HOLD", "CENTURY SAGE", "Q TECH", "PLOVER BAY TECH", "MICROWARE", "SIM TECH", "SINO GOLF HOLD", "CWT INT'L", "AUX INTL", "HAICHANG HLDG", "BESTWAY GLOBAL", "HONMAGOLF", "LUK HING ENT", "ALPHA ERA", "BAR PACIFIC", "FIRST TRACTOR", "CHEN HSONG HOLD", "COSMOS MACH", "AVIC IHL", "JINGCHENG MAC", "KUNMING MACHINE", "UNIS HOLDINGS", "LEEPORT(HOLD)", "LK TECH", "ZMJ", "C HIGHPRECISION", "SANY INT'L", "CHTC FONG'S INT", "ASIA TELE-NET", "TIANYE WATER", "EAGLE LEGEND", "CYBERNAUT INT'L", "ZOOMLION", "SUNLIT SCI", "D&G TECH", "CW GROUP HOLD", "HAO TIAN INTL", "AP RENTALS", "YUK WING GP", "TSUGAMI CHINA", "HUAZHANG TECH", "CRCCE", "HAITIAN INT'L", "CIMC", "GOOD FRIEND", "JINHUI HOLDINGS", "CHINA MER PORT", "OOIL", "ASIA ENERGY LOG", "SINOTRANS SHIP", "COSCO SHIP INTL", "CHU KONG SHIP", "SINOTRANS", "COSCO SHIP ENGY", "COURAGE INV", "COSCO SHIP PORT", "EVER HARVEST GP", "CIG PORTS", "COSCO SHIP HOLD", "XINGHUA PORT", "PACIFIC BASIN", "COSCO SHIP DEV", "DALIAN PORT", "QHD PORT", "XIAMEN PORT", "TIANJINPORT DEV", "GREAT HARVEST", "QINGDAO PORT", "UNITAS HOLD", "XIANGXING INT", "MY MEDICARE", "CS HEALTH", "CHINA MED&HCARE", "HUAYI TENCENT", "WAH YAN-NEW", "CHINA HEALTH", "UMP", "GOLDEN MEDITECH", "MICROPORT", "LIFE HEALTHCARE", "WEIGAO GROUP", "TECHCOMP", "LIFETECH SCI", "PW MEDTECH", "HUMAN HEALTH", "HARMONICARE", "CRPHOENIXHEALTH", "NC HEALTHCARE", "RICI HEALTH", "VINCENT MED", "SISRAM MED", "AK MEDICAL", "CHUNLI MEDICAL", "KN HOSPITAL", "UM HEALTHCARE", "BAMBOOSHEALTH", "BJ ENT M&H", "YESTAR HEALTH", "WAH YAN-OLD", "C-MER EYE", "PAK FAH YEOW", "WANJIA GROUP", "TIANDA PHARMA", "SIHUAN PHARM", "LANSEN PHARMA", "CHINAGRANDPHARM", "TRAD CHI MED", "PASHUN INT'L", "REGENT PACIFIC", "HUA HAN HEALTH", "SHANDONG XINHUA", "EXTRAWELL PHAR", "CMS", "BAIYUNSHAN PH", "C ANIMAL HEALTH", "LEE'S PHARM", "NT PHARMA", "ESSEX BIO-TECH", "CSPC PHARMA", "SINOPHARM", "KINGWORLD", "SINO BIOPHARM", "PIONEER PHARM", "PURAPHARM", "LIVZON PHARMA", "HEC PHARM", "TONG REN TANG", "CONSUN PHARMA", "SANAI HEALTH GP", "SSY GROUP", "V1 GROUP", "SMI HOLDINGS", "MEI AH ENTER", "EMPEROR CULTURE", "DIGITAL DOMAIN", "ESUN HOLDINGS", "A8 NEW MEDIA", "HUANXI MEDIA", "UNI INTL FIN", "ALI PICTURES", "ORANGE SKY G H", "HKTV", "PEGASUS ENT", "CHINA ANIMATION", "IMAX CHINA", "BIRMINGHAM SPTS", "SMI CULTURE", "MEDIA ASIA", "HMV DIGIT CHINA", "LAJIN ENT", "BINGO GROUP", "NATIONAL ARTS", "GDC", "CREATIVE CHINA", "ITP HOLDINGS", "WISDOM SPORTS", "TOM GROUP", "POLY CULTURE", "XINGFA ALUM", "GREENTECH INTL", "CHINA CHENGTONG", "JIANGXI COPPER", "NORTH MINING", "RUSAL", "HUAN YUE INTER", "YUE DA MINING", "LEE KEE", "CDAYENONFER", "CHINA RAREEARTH", "KAZ MINERALS-S", "CHIHO ENV", "CST GROUP", "MIDAS HLDGS-S", "SHENGLI PIPE", "CITIC DAMENG", "CGN MINING", "MMG", "ZHIDAO INT'L", "CHINFMINING", "HUILI RES", "CHINA ZHONGWANG", "CHINAHONGQIAO", "CMRU", "PANASIALUM", "CH POLYMETAL", "JINCHUAN INTL", "CHALCO", "XINXIN MINING", "ALLIED PPT (HK)", "GET NICE", "SUN HUNG KAI CO", "GOODRESOURCES", "HK BLDG & LOAN", "GOLDBOND GROUP", "STYLAND HOLD", "CHINA STRATEGIC", "VONGROUP", "UPBEST GROUP", "ALLIED GROUP", "EG LEASING", "HKEX", "JUN YANG FIN", "CHINA SDHS FIN", "HAO TIAN DEV", "DINGYI GP INV", "GOLDIN FIN HOLD", "DETAI NEWENERGY", "C FIN SERVICES", "ENERCHINA HOLD", "GOME FIN TECH", "DOYEN INTL HOLD", "SHOUGANG GRAND", "ETERNITY INV", "SINO PROSPER GP", "PACIFIC PLYWOOD", "AEON CREDIT", "HUARONG INT FIN", "CONVOY", "BROCKMAN MINING", "DINGHE MINING", "INFINITY FIN", "CHI KINGSTONE", "FB MINING", "CNNC INT'L", "NPE HOLDINGS", "ARTGO HOLDINGS", "EVERSHINE GP", "PIZU GROUP", "FS NONMETAL", "CROSS-HAR(HOLD)", "CENTURY LEGEND", "PICO FAR EAST", "CHINA EDU GROUP", "HK EDU INTL", "MAPLELEAF EDU", "INT'L ELITE", "MEGAEXPO HLDG", "BAGUIO GREEN", "VIRSCEND EDU", "MINSHENG EDU", "CHINA LEON", "PINE CARE GP", "NEW HIGHER EDU", "KAKIKO GROUP", "CL EDU FIN", "MING FAI INT'L", "WISDOM EDU INTL", "UTS MARKETING", "YUHUA EDU", "KPM HOLDING", "VIVA CHINA", "CHINAOCEAN FISH", "ROMA GROUP", "TSO", "GOLDWAY EDU", "CHINA ECO-FARM", "PPS INT'L", "CCID CONSULTING", "YIN HE HLDGS", "HK FOOD INV", "FIRST PACIFIC", "LIPPO CHINA RES", "HENG TAI", "TINGYI", "SHENGUAN HLDGS", "KANGDA FOOD", "YURUN FOOD", "FRESH EXP", "CHRISTINE", "LABIXIAOXIN", "JIASHILI GP", "ZHOU HEI YA", "NISSIN FOODS", "QINQIN FOODS", "C SHENGHAI FOOD", "DALI FOODS", "TIANYUN INT'L", "CHINA CANDY", "WINNING TOWER", "COOL LINK", "SAMSON PAPER", "COME SURE GROUP", "IWS", "CHENMING PAPER", "SUNSHINE PAPER", "CG DUNXIN", "YOUYUAN HLDGS", "LEE & MAN PAPER", "HOP FUNG GROUP", "ND PAPER", "HOIFU ENERGY", "INT'L STD RES", "KUNLUN ENERGY", "NEWTIMES ENERGY", "CHINA ENERGY", "AVIC JOY HLDG", "YUANHENG GAS", "SHANGHAI PECHEM", "NEWOCEAN ENERGY", "YANCHANG PETRO", "ENERGY INTINV", "SINOPEC CORP", "UNITEDENERGY GP", "PEARLORIENT OIL", "IDG ENERGY", "KING STONE ENGY", "EPI (HOLDINGS)", "SINO OIL & GAS", "TOURONG CHANGFU", "STRONG PETRO", "PETROCHINA", "CNOOC", "BRIGHTOIL", "SINOPEC KANTONS", "DASHENG AGR FIN", "TITAN PETROCHEM", "CITIC RESOURCES", "MIE HOLDINGS", "SUNSHINE OIL", "UNITED STRENGTH", "HONGHUA GROUP", "TSC GROUP", "HANS ENERGY", "SHANDONG MOLONG", "SINOPEC SSC", "SPT ENERGY", "HILONG", "CHU KONG PIPE", "PETRO-KING", "WISON ENGRG", "CHINA OILFIELD", "JUTAL OIL SER", "ANTON OILFIELD", "REALGOLD MINING", "C BILLION RES", "TAUNG GOLD", "CHI SILVER GP", "MUNSUN CAPITAL", "ZHAOJIN MINING", "CHINAGOLDINTL", "HENGXING GOLD", "ZIJIN MINING", "LINGBAO GOLD", "TIMELESS", "LOCO HK", "GRAND T G GOLD", "NEWAY GROUP", "HUAJUN HOLD", "STARLITE HOLD", "HUNG HING PRINT", "GAPACK", "COFCO PACKAGING", "HJ CAPITAL INTL", "BRILLIANT CIR", "SINO HAIJING", "LION ROCK GROUP", "MIDAS INT'L", "TESSON HOLDINGS", "TEAMWAY INTL GP", "CHINA TOUYUN", "SHEEN TAI", "MOBILE INTERNET", "TOURISM INTL", "MENGKE HLDGS", "REF HOLDINGS", "HUAXI HOLD-NEW", "EPRINT GROUP", "SUN HING PRINT", "AMVIG HOLDINGS", "HUAXI HOLD-OLD", "ZHENGYE INT'L", "HSSP INTL", "CHINA ALUMCAN", "A.PLUS GROUP", "LINOCRAFT HLDGS", "PROSPEROUSPRINT", "HENDERSON LAND", "SHK PPT", "NEW WORLD DEV", "WHEELOCK", "GREAT CHI PPT", "TIAN AN", "KOWLOON DEV", "FE CONSORT INTL", "HK FERRY (HOLD)", "HARBOUR CENTRE", "SKYFAME REALTY", "CHINA GRAPHENE", "CH OVS G OCEANS", "SINO LAND", "TAI CHEUNG HOLD", "LVGEM CHINA", "GRAND FIELD GP", "POLY PROPERTY", "COSMOPOL INT'L", "YUEXIU PROPERTY", "GD LAND", "CHINESE EST H", "ASIA STANDARD", "IB SETTLEMENT", "HON KWOK LAND", "EMPEROR INT'L", "WANDA HOTEL DEV", "K. WAH INT'L", "WINFULL GP", "HKC (HOLDINGS)", "NINE EXPRESS", "HANG LUNG GROUP", "HYSAN DEV", "CHINA MOTOR BUS", "DYNAMIC HOLD", "GREAT EAGLE H", "Y.T. REALTY", "TAI SANG LAND", "TERMBRAY IND", "HANG LUNG PPT", "ASSO INT HOTELS", "LANDSEA PPT", "LT COMM REALEST", "CHEUK NANG HOLD", "GREAT CHINA", "CH AGRI-PROD EX", "MELBOURNE ENT", "SILVER GRANT", "ZH INT'L HOLD", "LAI SUN INT'L", "LIU CHONG HING", "NANYANG HOLD", "ASIA ORIENT", "CHINNEY INV", "PIONEER GLOBAL", "POKFULAM", "SAFETY GODOWN", "TST PROPERTIES", "SEA HOLDINGS", "TIAN TECK LAND", "GR PROPERTIES", "FORTUNE SUN", "MIDLAND IC&I", "HOPEFLUENT", "MIDLAND HOLDING", "RIVERINE CHINA", "ZHONG AO HOME", "LHN", "COLOUR LIFE", "CHINA OVS PPT", "GREENTOWN SER", "A-LIVING", "CLIFFORDML", "KONGSHUMUNION", "FINELANDSERVICE", "MODERN LIVING", "TRANSPORT INT'L", "MTR CORPORATION", "AMS TRANSPORT", "KWOON CHUNG BUS", "CAR INC", "ORIENTAL PRESS", "MODERN MEDIA", "NEXT DIGITAL", "XH NEWS MEDIA", "CULTURECOM HOLD", "HKET HOLDINGS", "ONE MEDIA GROUP", "MEDIA CHINESE", "EVERG HEALTH", "XINHUA WINSHARE", "ROADSHOW", "SING TAO", "SINO SPLENDID", "YUEXIU REIT", "SUNLIGHT REIT", "RREEF CCT REIT", "FORTUNE REIT", "PROSPERITY REIT", "LINK REIT", "NEW CENT REIT", "SPRING REIT", "REGAL REIT", "CHAMPION REIT", "HUI XIAN REIT", "HOP HING GROUP", "FAIRWOOD HOLD", "CAFE DE CORAL H", "XIABUXIABU", "AJISEN (CHINA)", "TAO HEUNG HLDGS", "G-VISION INT'L", "FUTURE BRIGHT", "TANG PALACE", "TSUI WAH HLDG", "FULUM GP HLDG", "U BANQUET GP", "FOODWISE HLDG", "TANSH", "DINING CONCEPTS", "LI BAO GE GP", "FOOD IDEA", "NEW WISDOM H", "CLASSIFIED GP", "C FOOD&BEV GP", "ROYALCATERING", "SIMPLICITY HLDG", "TASTEGOURMET GP", "BCI GROUP", "CBK HOLDINGS", "DRAGON KING GP", "1957 & CO.", "TOPSTANDARDCORP", "JIA GROUP", "ELIFE HLDGS", "G CHINA FIN", "NOBLE CENTURY", "ASIA GROCERY", "SICHUAN EXPRESS", "SHENZHEN INT'L", "JIANGSU EXPRESS", "CRTG", "GUANGSHEN RAIL", "SHENZHENEXPRESS", "ZHEJIANGEXPRESS", "HOPEWELL INFR", "ANHUIEXPRESSWAY", "YUEXIUTRANSPORT", "HUAYUEXPRESSWAY", "CHINA CRSC", "FREETECH", "HOPEWELL INFR-R", "APT SATELLITE", "ASIA SATELLITE", "AVICHINA", "CINDA INTL HLDG", "SUNWAH KINGSWAY", "SHENWANHONGYUAN", "FIRST SHANGHAI", "PING AN SEC GP", "CN MINSHENG FIN", "GT GROUP HLDG", "FREEMAN FINTECH", "C FORTUNE FIN", "YUNFENG FIN", "CASH FIN SER GP", "IMAGI INT'L", "HAITONG INT'L", "EMPEROR CAPITAL", "PINESTONE", "SWSI", "VC HOLDINGS", "OCEANWIDE FIN", "CMBC CAPITAL", "CC SECURITIES", "BRIGHT SMART", "GUOLIAN SEC", "LUZHENG FUTURES", "GET NICE FIN", "HENGTOU SEC", "GF SEC", "GUOTAI JUNAN I", "GTJA", "BOCOM INTL", "CICC", "CE HUADA TECH", "CH SOFT POWER", "QPL INT'L", "ASM PACIFIC", "AV CONCEPT HOLD", "RUIXIN INT'L", "SMIC", "PACRAY INT'L", "HUA HONG SEMI", "SHANGHAI FUDAN", "PENTAMASTER", "RISECOMM GP", "SMART-CORE", "TOP DYNAMIC", "HAILIANG INTL", "SOLOMON SYSTECH", "ASMC", "PFC DEVICE", "MEGALOGICTECH", "GENES TECH", "VERTICAL INT'L", "CHINA MINING", "HAISHENG JUICE", "BLOCKCHAIN-NEW", "SUMMI", "CHINA GREEN", "TIBET WATER", "HUNGFOOKTONG", "HUIYUAN JUICE", "ANDRE JUICE", "LONGRUN TEA", "BLOCKCHAIN-OLD", "TENFU", "COMPUTER & TECH", "KINGDEE INT'L", "NEW SPORTS", "PEACEMAP HOLD", "FOUNDER HOLD", "INSPUR INT'L", "RENTIAN TECH", "ANXIN-CHINA", "SINOSOFT TECH", "CHANJET", "ENTERPRISE DEV", "CHINA ITS", "KINGSOFT", "SUN INT'L", "YU TAK INT'L", "SING LEE", "GET HOLDINGS", "JIAN EPAYMENT", "CHINA INFO TECH", "AHSAY BACKUP", "BURWILL", "SHOUGANG CENT", "MAANSHAN IRON", "ANGANG STEEL", "CHINA ORIENTAL", "SHOUGANG INT'L", "PROSPERITY INTL", "TIANGONG INT'L", "CHINAVTM MINING", "IRC", "YORKSHINE HLDGS", "CHONGQING IRON", "DA MING INT'L", "APAC RESOURCES", "MAYER HOLDINGS", "GOLIK HOLDINGS", "GUANGNAN (HOLD)", "NEWTON RES", "XIWANG STEEL", "AOWEI HOLDING", "CAA RESOURCES", "ADD NEW ENERGY", "HUAJIN INTL", "NRI HOLDINGS", "CHINA HANKING", "HONBRIDGE", "WAN CHENG METAL", "CHAMPION TECH", "ALI HEALTH", "CHINASOFT INT'L", "KUANGCHI", "CH AUTOMATION", "CH ALL ACCESS", "TRAVELSKY TECH", "AUTOMATED SYS", "HI SUN TECH", "DC HOLDINGS", "VISION VALUES", "WAI CHUN GROUP", "MAXNERVA TECH", "KANTONE HOLDING", "CAPINFO", "TECHNOVATOR", "NATIONAL AGRI", "ICO GROUP", "BII TRANS TECH", "SUNEVISION", "SAMPLE TECH", "ECI TECH", "CHANGHONG JH", "FINSOFT FIN", "VODATEL NETWORK", "CHI E-INFO TECH", "GLOBAL LINK", "CH NETCOMTECH", "ITE HOLDINGS", "TRILLION GRAND", "CHINACOMSERVICE", "ZTE", "O-NET TECH GP", "SUNCORP TECH", "HXTL", "CENTRON TELECOM", "CHENGDU PUTIAN", "TRIGIANT", "SYNERTONE", "NANFANG COMM", "PUTIAN COMM", "TIME INTERCON", "COMBA", "DBA TELECOM", "C FIBER OPTIC", "TELECOM DIGIT", "CHINA U-TON", "BJ DIGITAL", "YOFC", "YUXING INFOTECH", "ATLINKS", "HAITIANTIAN", "PCCW", "HUTCHTEL HK", "SMARTONE TELE", "CMMB VISION", "E-KONG GROUP", "CHINA TELECOM", "CHINA UNICOM", "CHINA MOBILE", "HKBN", "CITIC TELECOM", "NNK", "HKT-SS", "ETS GROUP", "SHENGHUA LANDE", "NEO TELEMEDIA", "NETEL", "DIRECTEL", "NEXION TECH", "TAI PING CARPET", "EVERGREEN INT", "YANGTZEKIANG", "LUEN THAI", "TEXWINCA HOLD", "FOUNTAIN SET", "TRISTATE HOLD", "TUNGTEX (HOLD)", "KINGDOM HOLDING", "VICTORY CITY", "SPEEDY GLOBAL", "HIGH FASHION", "CARRY WEALTH", "CO-PROSPERITY", "GREATIME INTL", "C TAIFENG BED", "SFUND INTL HLDG", "PACIFICTEXTILES", "EMBRY HOLDINGS", "WANG TAI HLDG", "STARRISE MEDIA", "HUALONGJINKONG", "NAMESON HLDGS", "BEST PACIFIC", "GOLDEN SHIELD", "REGINA MIRACLE", "CASABLANCA", "CECEP COSTIN", "BILLION IND", "KAM HING INT'L", "HERALD HOLD", "KADER HOLDINGS", "WINSHINE SCI", "CHINAHEALTHWISE", "KIU HUNG INT'L", "SC HOLDINGS", "PLAYMATES", "PERFECTECH INTL", "PLAYMATES TOYS", "MATRIX HOLDINGS", "DREAM INT'L", "QUALI-SMART", "KIDSLAND INTL", "KIDDIELAND", "CHINA BAOLI TEC", "NEW CENTURY GP", "ORIENT VICTORY", "CHINA TRAVEL HK", "SUCCESSUNIVERSE", "GENTING HK", "TRAVEL EXPERT", "EGL HOLDINGS", "GLOBAL M HLDG", "WWPKG HOLDINGS", "GUDOU HLDGS", "TVB", "I-CABLE COMM", "PHOENIX TV", "HENG XIN CHINA", "CDV HOLDINGS", "FRONTIER SER", "DAIDO GROUP", "KERRY LOG NET", "BJ PROPERTIES", "DRAGON CROWN", "CMA LOGISTICS", "SITC", "BJ SPORTS & ENT", "WORLD-LINK-8K", "YUEYUN TRANS", "WORLD-LINK-4K", "ON TIME LOG", "JANCO HOLDINGS", "WORLDGATEGLOBAL", "DAFENG PORT", "BINHAI TEDA", "C&N HOLDINGS", "GOAL RISE", "STELUX HOLDINGS", "ASIA COMM HOLD", "CHOW SANG SANG", "CITYCHAMP", "KING FOOK HOLD", "ORIENTAL WATCH", "TSE SUI LUEN", "HIFOOD GROUP", "SINCEREWATCH HK", "ZHONG FA ZHAN", "CONTINENTAL H", "LUK FOOK HOLD", "DTXS SILK ROAD", "O LUXE HOLD", "EMPEROR WATCH&J", "TIME2U", "AFFLUENTPARTNER", "PROSPER ONE", "ERNEST BOREL", "CHOW TAI FOOK", "TIME WATCH", "HKRH", "PERFECT GROUP", "HENGDELI", "LARRY JEWELRY", "GUANGDONG INV", "BJ ENT WATER", "CHINA ENV TEC", "CHINA WATER", "SOUND GLOBAL", "UNIVERSAL TECH", "TIANJIN CAPITAL", "WATER INDUSTRY", "CTEG", "ELL ENV", "XINGLU WATER", "DCWT", "KANGDA ENV", "YUNNAN WATER"};
    public static final String[] stockNameChAll = {"中國汽車內飾", "中璽國際", "領視控股", "高雅光學", "時代集團控股", "粵海制革", "飛達控股", "雅視光學", "英裘控股", "百福控股", "新秀麗", "KEE", "華新手袋國際控股", "盈利時", "白馬戶外媒體", "財訊傳媒", "ＫＫ文化", "中視金橋", "中國國家文化產業", "品牌中國", "北青傳媒", "十方控股", "雅仕維", "中國三三傳媒", "奧傳思維控股", "基石金融", "超凡網絡", "匯創控股", "惠陶集團", "大賀傳媒", "HYPEBEAS", "卜蜂國際", "亞洲果業", "萬洲國際", "南順（香港）", "中國糧油控股", "偉俊礦業集團", "金源米業", "超大現代", "中國金控", "華聯國際", "長壽花食品", "太平洋恩利", "惠生國際", "中糧肉食", "普甜食品", "中國澱粉", "大成食品", "中國綠寶", "展程控股", "富譽控股", "香港飛機工程", "國泰航空", "中國東方航空股份", "中國國航", "中國南方航空股份", "中國飛機租賃", "中銀航空租賃", "航基股份", "北京首都機場股份", "青島啤酒股份", "香港生力啤", "華潤啤酒", "東建國際", "通天酒業", "新絲路文旅", "王朝酒業", "銀基集團", "美捷匯控股", "麥迪森控股", "中國幸福投資", "威揚酒業控股", "協合新能源", "江山控股", "協鑫新能源", "瑞風新能源", "中國核能科技", "熊貓綠能", "中國電力清潔能源", "華能新能源", "中國再生能源投資", "北控清潔能源集團", "隆基泰和智慧能源", "大唐新能源", "新特能源", "中國地能", "北方新能源", "同景新能源", "遠東控股國際", "迪生創建", "鱷魚恤", "安寧控股", "慕詩國際", "思捷環球", "黛麗斯國際", "ＹＧＭ貿易", "旭日企業", "包浩斯國際", "金利來集團", "寶國國際", "堡獅龍國際", "福晟國際", "JOYCEBOU", "佐丹奴國際", "利標品牌", "利邦", "道和環球", "國能國際資產", "榮暉國際", "N/A", "中國服飾控股", "米蘭站", "中國利郎", "米格國際控股", "諾奇", "３６１度", "國投集團控股", "華信金融投資", "新焦點", "意達利控股", "啟迪國際", "中升控股", "耀萊集團", "華多利集團", "美東汽車", "首控集團", "廣匯寶信", "潤東汽車", "正通汽車", "新豐泰集團", "大昌行集團", "永達汽車", "和諧汽車", "Ｇ﹒Ａ﹒控股", "正美豐業", "正力控股", "信義香港", "吉利汽車", "德昌電機控股", "五菱汽車", "越南製造加工出口", "敏實集團", "中國動力控股", "東風集團股份", "陳唱國際", "五龍電動車", "天能動力", "中大國際", "超威動力", "光宇國際集團科技", "浙江世寶", "華晨中國", "中國環境資源", "新晨動力", "漢唐國際控股", "正道集團", "偉祿集團", "比亞迪股份", "N/A", "耐世特", "比速科技", "信邦控股", "雅迪控股", "北京汽車", "瑞豐動力", "廣汽集團", "長城汽車", "匯豐控股", "恆生銀行", "東亞銀行", "錦州銀行", "大新金融", "大眾金融控股", "建設銀行", "中信銀行", "創興銀行", "中原銀行", "農業銀行", "工商銀行", "廣州農商銀行", "天津銀行", "郵儲銀行", "重慶銀行", "民生銀行", "浙商銀行", "盛京銀行", "甘肅銀行", "大新銀行集團", "N/A", "渣打集團", "交通銀行", "重慶農村商業銀行", "徽商銀行", "青島銀行", "招商銀行", "中國銀行", "BOCOM 15", "領航醫藥生物科技", "聯康生物科技集團", "ＢＢＩ生命科學", "復旦張江", "三生製藥", "金斯瑞生物科技", "藥明生物", "美瑞健康國際", "昊海生物科技", "香港生命科學", "中國再生醫學", "中生北控生物科技", "旭光高新材料", "東岳集團", "中國天化工", "葉氏化工集團", "天德化工", "星謙發展", "北海集團", "理文化工", "大成生化科技", "常茂生物", "毅興行", "迪諾斯環保", "萬輝化工", "天合化工", "中國龍天集團", "彩客化學", "一化控股", "中國三江化工", "中怡國際", "中國香精香料", "正大企業國際", "N/A", "西北實業", "綠領控股", "弘海高新資源", "國家聯合資源", "蒙古能源", "優派能源發展", "融信資源", "首鋼資源", "安域亞洲", "和嘉資源", "百營環球資源", "亞洲煤業", "中國秦發", "MONGOL M", "樓東俊安資源", "中國神華", "鴻寶資源", "西伯利亞礦業", "兗州煤業股份", "奇峰國際", "南南資源", "力量能源", "N/A", "中國優質能源", "易大宗", "飛尚無煙煤", "南戈壁－Ｓ", "中煤能源", "伊泰煤炭", "金馬能源", "凱順能源", "中船防務", "中海重工", "勝獅貨櫃", "華榮能源", "慶鈴汽車股份", "中國中車", "中國重汽", "中車時代電氣", "長和", "九龍倉集團", "太古股份公司Ａ", "萬隆控股集團", "合和實業", "南海石油", "太古股份公司Ｂ", "佳寧娜", "潤中國際控股", "閩信集團", "信德集團", "華信地產財務", "中信股份", "上海實業控股", "復星國際", "新創建集團", "中國唐商", "珠海控股投資", "長江基建集團", "時富投資", "隆成金融", "N/A", "國農金融投資", "盈信控股", "其士國際", "中國賽特", "豐盛機電", "建聯集團", "新昌集團控股", "有利集團", "雋泰控股", "香港國際建投", "亞洲聯合基建控股", "興業太陽能", "凌銳控股", "遠東環球", "興勝創建", "瑞安建業", "恆誠建築", "澳能建設", "青建國際", "保集健康", "中國綠地博大綠澤", "佳明集團控股", "N/A", "川控股", "新福港", "巨匠建設", "金誠控股", "思城控股", "集一家居", "前進控股集團", "現恆建築", "新威國際", "陸氏集團（越南）", "冠力國際", "山水水泥", "東吳水泥", "世界（集團）", "亞洲水泥（中國）", "莊勝百貨集團", "海螺水泥", "滬港聯合", "中國天瑞水泥", "同方康泰", "華潤水泥控股", "善樂國際", "中材股份", "金隅集團", "中國聯塑", "西部水泥", "中國建材", "御佳控股", "乙德投資控股", "N/A", "瑞強集團", "金山工業", "中國長遠", "萬威國際", "嘉域集團", "樂聲電子", "香港通訊國際控股", "比亞迪電子", "VTECH HO", "金寶通", "愛高集團", "國華", "中國華星", "權智（國際）", "華訊", "信佳國際", "富士高實業", "中港照相", "華夏健康產業", "通力電子", "新龍移動", "飛毛腿", "N/A", "弘達金融控股", "天寶集團", "富智康集團", "國微技術", "酷派集團", "天彩控股", "維太移動", "匯思太平洋", "自然美", "莎莎國際", "卓悅控股", "現代美容", "L'OCCITA", "恆安國際", "奧思集團", "威高國際", "中國兒童護理", "霸王集團", "必瘦站", "訓修實業", "格林國際控股", "維達國際", "衍生集團", "超人智能", "修身堂", "中國金典集團", "卓珈控股", "德寶集團控股", "彌明生活百貨", "天然乳品", "大慶乳業", "Ｈ＆Ｈ國際控股", "現代牧業", "雅士利國際", "原生態牧業", "中國聖牧", "中地乳業", "莊園牧場", "澳優", "蒙牛乳業", "輝山乳業", "中國北大荒", "恆基發展", "卜蜂蓮花", "世紀金花", "先施", "永安國際", "歲寶百貨", "國美零售", "佳華百貨控股", "ＣＥＣ國際控股", "北京京客隆", "新世界百貨中國", "利亞零售", "茂業國際", "中國順客隆", "聯華超市", "永旺", "利福國際", "華地國際控股", "利福中國", "益華控股", "N/A", "百盛集團", "高鑫零售", "中國旺旺", "統一企業中國", "維他奶國際", "四洲集團", "中國食品", "天喔國際", "美麗華酒店", "錦江酒店", "中國數碼信息", "百富環球", "中國智能集團", "科通芯城", "博雅互動", "雲遊控股", "貿易通", "太平洋網絡", "未來世界金融", "騰訊控股", "閱文集團", "網龍", "IGG", "中國錢包", "飛魚科技", "鑫網易商", "樂遊科技控股", "中國公共採購", "雷蛇", "美圖公司", "天鴿互動", "N/A", "百奧家庭互動", "慧聰網", "艾伯科技", "阜博集團", "聯眾", "環球戰略集團", "俊盟國際", "新維國際控股", "東北電氣", "賽晶電力電子", "中國高速傳動", "恆都集團", "東方電氣", "哈爾濱電氣", "中國航天萬源", "格菱控股", "江南集團", "博耳電力", "泰坦能源技術", "金風科技", "上海電氣", "中國能源建設", "中電控股", "電能實業", "琥珀能源", "京能清潔能源", "華電福新", "華潤電力", "天津發展", "華能國際電力股份", "龍源電力", "大唐發電", "華電國際電力股份", "粵豐環保", "偉能集團", "內蒙古能建", "海天能源", "中廣核新能源", "中廣核電力", "中國電力", "港燈－ＳＳ", "天利控股集團", "凱普松國際", "威雅利", "美加醫學", "連達科技控股", "萬裕科技", "時捷", "萬保剛集團", "申基國際", "揚宇科技", "創新電子控股", "中國源暢", "彩虹新能源", "漢能薄膜發電", "卡姆丹克太陽能", "陽光能源", "木薯資源", "環能國際", "順風清潔能源", "創益太陽能", "保利協鑫能源", "興業新材料", "中化化肥", "世紀陽光", "玖源集團", "浩倫農科", "東光化工", "中國心連心化肥", "中海石油化學", "泰達生物", "華寶國際", "阜豐集團", "頤海國際", "椰豐集團", "老恆和釀造", "味丹國際", "大成糖業", "達芙妮國際", "裕元集團", "創信國際", "萊爾斯丹", "邵氏兄弟控股", "千百度", "中能國際控股", "寶峰時尚", "信星集團", "新灃集團", "港大零售", "特步國際", "富貴鳥", "九興控股", "安踏體育", "李寧", "永駿國際控股", "綠心集團", "永保林業", "中國農林低碳", "大森控股", "中國優材", "世大控股", "同仁資源", "鴻偉亞洲", "安賢園中國", "福壽園", "仁智國際集團", "福澤集團", "中國萬桐園", "中國生命集團", "興利（香港）控股", "建福集團", "卡森國際", "利時集團控股", "順誠", "怡邦行控股", "創科實業", "中國家居", "帝國集團環球控股", "譚木匠", "中國集成控股", "好孩子國際", "航標控股", "皇朝傢俬", "中科生物", "NEWTREE", "國際家居零售", "盛諾集團", "匯能集團", "慕容控股", "中創環球", "N/A", "同方友友", "敏華控股", "浩澤淨水", "中國綠島科技", "大自然家居", "雷士照明", "中國寶豐國際", "科勁國際", "銀河娛樂", "金粵控股", "凱升控股", "新濠國際發展", "英皇娛樂酒店", "中國星集團", "御泰中彩控股", "藍鼎國際", "澳博控股", "奧瑪仕國際", "博華太平洋", "永利澳門", "星光文化", "匯彩控股", "勝龍國際", "NIRAKU", "華彩控股", "OKURA HO", "澳門勵駿", "金沙中國有限公司", "美高梅中國", "N/A", "DYNAM JA", "眾彩股份", "樂透互娛", "亞博科技控股", "亞洲先鋒娛樂", "香港中華煤氣", "中國燃氣", "北京控股", "中國基建投資", "中油燃氣", "中民控股", "新天綠色能源", "港華燃氣", "華潤燃氣", "天津津燃公用", "蘇創燃氣", "天倫燃氣", "大眾公用", "新奧能源", "濱海投資", "中裕燃氣", "北京燃氣藍天", "中國基礎能源", "新興光學", "長江生命科技", "位元堂", "前海健康", "碧生源", "順騰國際控股", "意科控股", "匯銀控股集團", "瑞年國際", "澳至尊", "中生聯合", "康健國際醫療", "茂盛控股", "遠東酒店實業", "大酒店", "香格里拉（亞洲）", "富豪國際", "中國興業控股", "閩港控股", "激成投資", "冠中地產", "華大酒店", "順豪物業", "順豪控股", "泛海酒店", "世紀城市國際", "新嶺域集團", "天元醫療", "百利保控股", "皇冠環球集團", "珀麗酒店", "開源控股", "信和酒店", "N/A", "朸濬國際", "金茂酒店－ＳＳ", "華星控股", "利民實業", "志高控股", "建溢集團", "亞倫國際", "創維數碼", "海信科龍", "ＴＣＬ多媒體", "海爾電器", "匯銀智慧社區", "久融控股", "英馬斯集團", "環球能源資源", "壹照明", "航天控股", "YUSEI", "恆騰網絡", "建滔化工", "龍記集團", "中國消防", "達進東方照明", "WKKINTL(", "大昌微線集團", "堅寶國際", "嘉瑞國際", "億和控股", "理士國際", "信義玻璃", "國際精密", "信義光能", "威鋮國際", "洛陽玻璃股份", "依利安達", "星凱控股", "中國恆石", "N/A", "翼辰實業", "桐成控股", "安捷利實業", "億仕登控股", "致豐工業電子", "建滔積層板", "興達國際", "中漆集團", "利基控股", "中國中鐵", "惠記集團", "瀋陽公用發展股份", "中國疏浚環保", "中國鐵建", "工蓋有限公司", "建業建榮", "均安控股", "城建設計", "中國交通建設", "中國機械工程", "萬景控股", "瑞港建設", "建禹集團", "中國新華電視", "浙江聯合投資", "亞洲金融", "宏利金融－Ｓ", "中國太平", "友邦保險", "新華保險", "中國人民保險集團", "中國再保險", "中國平安", "中國財險", "保誠", "中國太保", "中國人壽", "眾安在線", "泰加保險", "亞投金融集團", "利豐", "高山企業", "大洋環球控股", "國浩集團", "利興發展", "中國新經濟投資", "招商局中國基金", "中建富通", "中國光大控股", "盛洋投資", "中國投資開發", "茂宸集團", "嘉進投資國際", "安利時投資", "鼎立資本", "保德國際發展", "五龍動力", "匯嘉中國", "東方網庫", "中國投資基金公司", "渝港國際", "南華金融", "新工投資", "太和控股", "N/A", "開明投資", "SHANGHAI", "惠理集團", "中國互聯網投資", "盛源控股", "鷹力投資", "環球大通投資", "合一投資", "王氏國際", "國安國際", "先機企業集團", "億都（國際控股）", "中建置地", "華顯光電", "富通科技", "華建控股", "聖馬丁國際", "新龍國際", "南京熊貓電子股份", "通達集團", "京東方精電", "信利國際", "偉仕佳杰", "冠捷科技", "摩比發展", "融達控股", "聯想集團", "嘉利國際", "松景科技", "N/A", "柏能集團", "中國金洋", "偉志控股", "世紀睿科", "丘鈦科技", "珩灣科技", "美高域", "晨訊科技", "順龍控股", "CWT INT'", "奧克斯國際", "海昌海洋公園", "榮威國際", "本間高爾夫", "陸慶娛樂", "合寶豐年", "太平洋酒吧", "第一拖拉機股份", "震雄集團", "大同機械", "中航國際控股", "京城機電股份", "昆明機床", "紫光控股", "力豐（集團）", "力勁科技", "鄭煤機", "中國高精密", "三一國際", "中國恆天立信國際", "亞洲聯網科技", "天業節水", "鵬程亞洲", "賽伯樂國際控股", "中聯重科", "盛力達科技", "德基科技控股", "創達科技控股", "N/A", "亞積邦租賃", "煜榮集團", "津上機床中國", "華章科技", "鐵建裝備", "海天國際", "中集集團", "友佳國際", "金輝集團", "招商局港口", "東方海外國際", "亞洲能源物流", "中外運航運", "中遠海運國際", "珠江船務", "中國外運", "中遠海能", "勇利投資", "中遠海運港口", "永豐集團控股", "中國基建港口", "中遠海控", "興華港口", "太平洋航運", "中遠海發", "大連港", "秦港股份", "廈門港務", "天津港發展", "N/A", "青島港", "宏海控股集團", "象興國際", "銘源醫療", "同佳健康", "中國醫療網絡", "華誼騰訊娛樂", "中國華仁醫療－新", "中國衛生集團", "聯合醫務", "金衛醫療", "微創醫療", "蓮和醫療", "威高股份", "天美（控股）", "先健科技", "普華和順", "盈健醫療", "和美醫療", "華潤鳳凰醫療", "新世紀醫療", "瑞慈醫療", "永勝醫療", "SISRAM M", "N/A", "春立醫療", "康寧醫院", "香港醫思醫療集團", "百本醫護", "北控醫療健康", "巨星醫療控股", "中國華仁醫療－舊", "希瑪眼科", "白花油", "萬嘉集團", "天大藥業", "四環醫藥", "朗生醫藥", "遠大醫藥", "中國中藥", "百信國際", "勵晶太平洋", "華瀚健康", "山東新華製藥股份", "精優藥業", "康哲藥業", "白雲山", "中國動物保健品", "李氏大藥廠", "泰凌醫藥", "億勝生物科技", "石藥集團", "國藥控股", "金活醫藥集團", "N/A", "中國先鋒醫藥", "培力控股", "麗珠醫藥", "東陽光藥", "同仁堂科技", "康臣葯業", "三愛健康集團", "石四藥集團", "第一視頻", "星美控股", "美亞娛樂資訊", "英皇文化產業", "數字王國", "豐德麗控股", "Ａ８新媒體", "歡喜傳媒", "寰宇國際金融", "阿里影業", "橙天嘉禾", "香港電視", "天馬影視", "華夏動漫", "IMAX CHI", "伯明翰體育", "星美文化旅遊", "寰亞傳媒", "ＨＭＶ數碼中國", "拉近網娛", "比高集團", "N/A", "環球數碼創意", "中國創意控股", "ITP HOLD", "智美體育", "ＴＯＭ集團", "保利文化", "興發鋁業", "綠科科技國際", "中國誠通發展集團", "江西銅業股份", "北方礦業", "俄鋁", "歡悅互娛", "悅達礦業", "利記", "中國大冶有色金屬", "中國稀土", "哈薩克礦業－Ｓ", "齊合環保", "中譽集團", "麥達斯控股－Ｓ", "勝利管道", "中信大錳", "中廣核礦業", "五礦資源", "志道國際", "中國有色礦業", "N/A", "中國忠旺", "中國宏橋", "中國金屬利用", "榮陽實業", "中國多金屬", "金川國際", "中國鋁業", "新疆新鑫礦業", "聯合地產（香港）", "結好控股", "新鴻基公司", "天成國際", "香港建屋貸款", "金榜集團", "大凌集團", "中策集團", "黃河實業", "美建集團", "聯合集團", "恆嘉融資租賃", "香港交易所", "君陽金融", "中國山東高速金融", "昊天發展集團", "鼎億集團投資", "高銀金融", "德泰新能源集團", "中國金融投資管理", "威華達控股", "N/A", "東銀國際控股", "首長四方", "永恆策略", "中盈集團控股", "PACIFIC", "ＡＥＯＮ信貸財務", "華融金控", "康宏環球", "布萊克萬礦業", "鼎和礦業", "新融宇集團", "中國金石", "高鵬礦業", "中核國際", "新源萬恆控股", "雅高控股", "永耀集團控股", "比優集團", "飛尚非金屬", "港通控股", "世紀建業", "筆克遠東", "中教控股", "香港教育國際", "楓葉教育", "精英國際", "MEGAEXPO", "碧瑤綠色集團", "成實外教育", "民生教育", "中國力鴻", "松齡護老集團", "新高教集團", "KAKIKO G", "創聯教育金融", "明輝國際", "睿見教育", "UTS MARK", "宇華教育", "吉輝控股", "N/A", "中國海洋捕撈", "羅馬集團", "電訊首科", "GOLDWAY", "中國農業生態", "寶聯控股", "賽迪顧問", "銀合控股", "香港食品投資", "第一太平", "力寶華潤", "亨泰", "康師傅控股", "神冠控股", "康大食品", "雨潤食品", "鮮馳達控股", "克莉絲汀", "蠟筆小新食品", "嘉士利集團", "周黑鴨", "日清食品", "親親食品", "中國升海食品", "達利食品", "天韻國際控股", "中國糖果", "運興泰集團", "COOL LIN", "森信紙業集團", "錦勝集團（控股）", "綜合環保集團", "晨鳴紙業", "陽光紙業", "長港敦信", "優源控股", "理文造紙", "合豐集團", "玖龍紙業", "凱富能源", "標準資源控股", "昆侖能源", "新時代能源", "中能控股", "幸福控股", "元亨燃氣", "上海石油化工股份", "新海能源", "延長石油國際", "能源國際投資", "中國石油化工股份", "聯合能源集團", "東方明珠石油", "ＩＤＧ能源", "金山能源", "長盈集團（控股）", "中國油氣控股", "投融長富", "海峽石油化工", "中國石油股份", "N/A", "光匯石油", "中石化冠德", "大生農業金融", "泰山石化", "中信資源", "ＭＩ能源", "陽光油砂", "眾誠能源", "宏華集團", "ＴＳＣ集團", "漢思能源", "山東墨龍", "中石化油服", "華油能源", "海隆控股", "珠江鋼管", "百勤油服", "惠生工程", "中海油田服務", "巨濤海洋石油服務", "安東油田服務", "瑞金礦業", "中富資源", "壇金礦業", "中國白銀集團", "麥盛資本", "招金礦業", "中國黃金國際", "恆興黃金", "紫金礦業", "靈寶黃金", "天時軟件", "港銀控股", "大唐潼金", "中星集團控股", "華君控股", "星光集團", "鴻興印刷集團", "紛美包裝", "中糧包裝", "華金國際資本", "貴聯控股", "中國海景", "獅子山集團", "勤達集團國際", "天臣控股", "TEAMWAY", "中國透雲", "順泰控股", "移動互聯（中國）", "旅業國際", "盟科控股", "REF HOLD", "華禧控股（新）", "ＥＰＲＩＮＴ集團", "N/A", "澳科控股", "華禧控股（舊）", "正業國際", "HSSP INT", "中國鋁罐", "優越集團控股", "東駿控股", "萬里印刷", "恆基地產", "新鴻基地產", "新世界發展", "會德豐", "大中華地產控股", "天安", "九龍建業", "遠東發展", "香港小輪（集團）", "海港企業", "天譽置業", "中國烯谷集團", "中國海外宏洋集團", "信和置業", "大昌集團", "綠景中國地產", "鈞濠集團", "保利置業集團", "COSMOPOL", "越秀地產", "粵海置地", "N/A", "泛海集團", "國際商業結算", "漢國置業", "英皇國際", "萬達酒店發展", "嘉華國際", "宏輝集團", "香港建設（控股）", "九號運通", "恆隆集團", "希慎興業", "中華汽車", "達力集團", "鷹君", "渝太地產", "大生地產", "添利工業", "恆隆地產", "凱聯國際酒店", "朗詩綠色地產", "勒泰商業地產", "卓能（集團）", "大中華集團", "中國農產品交易", "萬邦投資", "銀建國際", "正恆國際控股", "麗新國際", "廖創興企業", "N/A", "匯漢控股", "建業實業", "建生國際", "博富臨置業", "安全貨倉", "尖沙咀置業", "爪哇控股", "天德地產", "國銳地產", "富陽", "美聯工商舖", "合富輝煌", "美聯集團", "浦江中國", "中奧到家", "LHN", "彩生活", "中海物業", "綠城服務", "雅生活服務", "祈福生活服務", "港深聯合", "方圓房服集團", "雅居投資控股", "載通", "港鐵公司", "進智公共交通", "冠忠巴士集團", "神州租車", "東方報業集團", "現代傳播", "壹傳媒", "新華通訊頻媒", "文化傳信", "經濟日報集團", "萬華媒體", "世界華文媒體", "恆大健康", "新華文軒", "路訊通", "星島", "華泰瑞銀", "越秀房產信託基金", "陽光房地產基金", "睿富房地產基金", "置富產業信託", "泓富產業信託", "領展房產基金", "開元產業信託", "春泉產業信託", "富豪產業信託", "冠君產業信託", "匯賢產業信託", "合興集團", "大快活集團", "大家樂集團", "呷哺呷哺", "味千（中國）", "稻香控股", "環科國際", "FUTUREBR", "唐宮中國", "翠華控股", "富臨集團控股", "譽宴集團", "膳源控股", "國際天食", "飲食概念", "利寶閣集團", "新煮意控股", "新智控股", "CLASSIFI", "華人飲食集團", "皇璽餐飲集團", "N/A", "嚐高美集團", "高門集團", "國茂控股", "龍皇集團", "1957 & C", "TOPSTAND", "JIA GROU", "易生活控股", "大中華金融", "仁瑞投資", "亞洲雜貨", "四川成渝高速公路", "深圳國際", "江蘇寧滬高速公路", "中國資源交通", "廣深鐵路股份", "深圳高速公路股份", "浙江滬杭甬", "合和公路基建", "安徽皖通高速公路", "越秀交通基建", "華昱高速", "中國通號", "英達公路再生科技", "合和公路基建－Ｒ", "亞太衛星", "亞洲衛星", "中航科工", "信達國際控股", "新華匯富金融", "申萬宏源", "第一上海", "平安證券集團控股", "中國民生金融", "高富集團控股", "民眾金融科技", "中國富強金融", "雲鋒金融", "時富金融服務集團", "意馬國際", "海通國際", "英皇證券", "鼎石資本", "西證國際證券", "匯盈控股", "中國泛海金融", "民銀資本", "中州證券", "耀才證券金融", "N/A", "魯証期貨", "結好金融", "恆投證券", "廣發証券", "國泰君安國際", "國泰君安", "交銀國際", "中金公司", "中電華大科技", "中國軟實力", "QPL INT'", "ASMPACIF", "AVCONCEP", "瑞鑫國際集團", "中芯國際", "太睿國際控股", "華虹半導體", "上海復旦", "檳傑科達", "瑞斯康集團", "芯智控股", "泰邦集團", "海亮國際", "SOLOMONS", "先進半導體", "節能元件", "宏創高科", "靖洋集團", "弘浩國際控股", "中國礦業", "海升果汁", "區塊鏈集團（新）", "森美控股", "中綠", "西藏水資源", "鴻福堂", "匯源果汁", "安德利果汁", "龍潤茶", "區塊鏈集團（舊）", "天福", "科聯系統", "金蝶國際", "新體育", "天下圖控股", "方正控股", "浪潮國際", "仁天科技控股", "中國安芯", "中國擎天軟件", "暢捷通", "企展控股", "中國智能交通", "金山軟件", "太陽國際", "御德國際控股", "新利軟件", "智易控股", "華普智通", "中國信息科技", "亞勢備份", "寶威控股", "首長寶佳", "馬鞍山鋼鐵股份", "鞍鋼股份", "中國東方集團", "首長國際", "昌興國際", "天工國際", "中國鐵鈦", "鐵貨", "煜新控股", "重慶鋼鐵股份", "大明國際", "亞太資源", "美亞控股", "高力集團", "廣南（集團）", "新礦資源", "西王特鋼", "奧威控股", "優庫資源", "N/A", "華津國際控股", "鎳資源國際", "中國罕王", "洪橋集團", "萬成金屬包裝", "CHAMPION", "阿里健康", "中國軟件國際", "光啟科學", "中國自動化", "中國全通", "中國民航信息網絡", "自動系統", "高陽科技", "神州控股", "遠見控股", "偉俊集團控股", "雲智匯科技", "看通集團", "首都信息", "同方泰德", "國農控股", "揚科集團", "京投交通科技", "新意網集團", "三寶科技", "N/A", "長虹佳華", "匯財金融投資", "愛達利網絡", "中國網絡信息科技", "國聯通信", "中彩網通控股", "ITEHOLDI", "萬泰企業股份", "中國通信服務", "中興通訊", "昂納科技集團", "新確科技", "亨鑫科技", "星辰通信", "成都普天電纜股份", "俊知集團", "協同通信", "南方通信", "普天通信集團", "匯聚科技", "京信通信", "ＤＢＡ電訊", "中國光纖", "電訊數碼控股", "中國優通", "迪信通", "長飛光纖光纜", "裕興科技", "ATLINKS", "N/A", "電訊盈科", "和記電訊香港", "數碼通電訊", "中播控股", "E-KONGGR", "中國電信", "中國聯通", "中國移動", "香港寬頻", "中信國際電訊", "年年卡", "香港電訊－ＳＳ", "易通訊集團", "升華蘭德", "中國新電信", "金利通", "直通電訊", "NEXION T", "太平地氈", "長興國際", "長江製衣", "聯泰控股", "德永佳集團", "福田實業", "聯亞集團", "同得仕（集團）", "金達控股", "冠華國際控股", "迅捷環球控股", "達利國際", "恆富控股", "協盛協豐", "廣泰國際控股", "中國泰豐床品", "廣州基金國際控股", "互太紡織", "安莉芳控股", "宏太控股", "星宏傳媒", "N/A", "南旋控股", "超盈國際控股", "金盾控股", "維珍妮", "卡撒天嬌", "中國節能海東青", "百宏實業", "錦興國際控股", "興利集團", "開達集團", "瀛晟科學", "中國智能健康", "僑雄國際", "南華集團控股", "彩星集團", "威發國際", "彩星玩具", "美力時集團", "德林國際", "滉達富控股", "凱知樂國際", "童園國際", "中國寶力科技", "新世紀集團", "東勝旅遊", "香港中旅", "實德環球", "雲頂香港", "專業旅運", "東瀛遊", "環球大通集團", "縱橫遊控股", "古兜控股", "電視廣播", "有線寬頻", "鳳凰衛視", "恆芯中國", "中國數字視頻", "先豐服務集團", "大同集團", "嘉里物流", "北京建設", "龍翔集團", "長安民生物流", "海豐國際", "北京體育文化", "環宇物流（八千）", "粵運交通", "環宇物流（四千）", "先達國際物流", "駿高控股", "盛良物流", "大豐港", "濱海泰達物流", "春能控股", "健升物流中國", "寶光實業", "ASIA COM", "周生生", "冠城鐘錶珠寶", "景福集團", "東方表行集團", "謝瑞麟", "海福德集團", "SINCEREW", "中發展控股", "恆和集團", "六福集團", "大唐西市", "奧立仕控股", "英皇鐘錶珠寶", "時間由你", "錢唐控股", "富一國際控股", "依波路", "周大福", "時計寶", "N/A", "保發集團", "亨得利", "俊文寶石", "粵海投資", "北控水務集團", "中國環保科技", "中國水務", "桑德國際", "環球實業科技", "天津創業環保股份", "中國水業集團", "中滔環保", "強泰環保", "興瀘水務", "滇池水務", "康達環保", "雲南水務"};
// full version of HKstock market

    private ViewGroup container;
    private LayoutInflater inflater;

    String savedStockSym = "";

    public YahooSearchActivity() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        this.inflater = inflater;
        return onChangePortraitView();
    }

    private View onChangePortraitView() {
        if (container != null) {
            container.removeAllViewsInLayout();
        }
        View mView = inflater.inflate(R.layout.activity_detailed_quote, container, false);

        findViews(mView);
        initial();
        setUpMarketSpinner();

        final Button searchBtn = mView.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("info", "search button clicked");
                Log.e("inputtext", String.valueOf(autoCompleteTextView.getText()));
                if (stockHintAdapter.getCount() == 0) {
                    Log.e("noRecord", "No Record. Try Again.");
                    Toast.makeText(getActivity(), "No Record. Try Again.", Toast.LENGTH_SHORT).show();
                } else {
                    String selectedStockSym = "";
                    String inputText = autoCompleteTextView.getText().toString();
                    if (!inputText.contains(".")) {
                        selectedStockSym = stockHintAdapter.getItem(0).toString();
                        autoCompleteTextView.setText(selectedStockSym);
                    } else {
                        selectedStockSym = inputText;
                    }
                    Log.e("selectedStockSym", selectedStockSym);
                    savedStockSym = selectedStockSym;
                    new YahooSearchActivity.GetYahooRealtimeData().execute(selectedStockSym);
                    new YahooSearchActivity.GetHistoricalDataByJson().execute(selectedStockSym);

                    try {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        autoCompleteTextView.setOnKeyListener(new TextView.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            searchBtn.performClick();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        mChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), YahooSearchActivity_land.class);
                intent.putExtra("stockSymName", savedStockSym);
                startActivity(intent);

            }
        });

        marketSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setUpMarketSpinner();
                autoCompleteTextView.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        YahooRealtimeDataStruct recordPreference = new YahooRealtimeDataStruct();
        recordPreference = loadSearchRecordPreferences();
        if (recordPreference != null) {
            Log.e("recordPreference", recordPreference.toString());
        }

        if (recordPreference != null) {
            QuoteNum.setText(String.valueOf(recordPreference.getStockSym()));
            QuoteName.setText(String.valueOf(recordPreference.getCompanyName()));
            LastUpdatedTime.setText(String.valueOf(recordPreference.getLastUpdatedTime()));

            PreviousCloseNum.setText(String.valueOf(recordPreference.getPreviousClose()));
            OpenNum.setText(String.valueOf(recordPreference.getOpen()));
            HighNum.setText(String.valueOf(recordPreference.getHigh()));
            LowNum.setText(String.valueOf(recordPreference.getLow()));
            VolumnNum.setText(String.valueOf(recordPreference.getVolume()));

            BidNum.setText(String.valueOf(recordPreference.getBid()));
            AskNum.setText(String.valueOf(recordPreference.getAsk()));
            DayRangeNum.setText(String.valueOf(recordPreference.getDayRange()));
            WeekRangeNum.setText(String.valueOf(recordPreference.getWeekRange52()));
            AvgVolumnNum.setText(String.valueOf(recordPreference.getVolumeAvg()));

            MarketCapNum.setText(String.valueOf(recordPreference.getMarketCap()));
            BetaNum.setText(String.valueOf(recordPreference.getBeta()));
            PERationNum.setText(String.valueOf(recordPreference.getPeRatio()));
            EpsNum.setText(String.valueOf(recordPreference.getEps()));
            EraningDateNum.setText(String.valueOf(recordPreference.getEarningsDate()));


            currentPrice.setText(String.valueOf(recordPreference.getCurrent()));
            currentPriceChange.setText(String.valueOf(recordPreference.getChange()));

            char changeSign = String.valueOf(recordPreference.getChange()).charAt(0);

            if (changeSign == '+') {
                currentPriceChange.setTextColor(GREEN);
            }else if (changeSign == '-'){
                currentPriceChange.setTextColor(RED);
            }else{
                currentPriceChange.setTextColor(BLACK);
            }
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Search");
    }

    private void findViews(View mView) {
        mOutputText = mView.findViewById(R.id.resultTextView);
        autoCompleteTextView = mView.findViewById(R.id.autoInputNum);

        QuoteNum = mView.findViewById(R.id.QuoteNum);
        QuoteName = mView.findViewById(R.id.QuoteName);
        LastUpdatedTime = mView.findViewById(R.id.LastUpdatedTime);


        PreviousCloseNum = mView.findViewById(R.id.PreviousCloseNum);
        OpenNum = mView.findViewById(R.id.OpenNum);
        HighNum = mView.findViewById(R.id.HighNum);
        LowNum = mView.findViewById(R.id.LowNum);
        VolumnNum = mView.findViewById(R.id.VolumnNum);

        BidNum = mView.findViewById(R.id.BidNum);
        AskNum = mView.findViewById(R.id.AskNum);
        DayRangeNum = mView.findViewById(R.id.DayRangeNum);
        WeekRangeNum = mView.findViewById(R.id.WeekRangeNum);
        AvgVolumnNum = mView.findViewById(R.id.AvgVolumnNum);

        MarketCapNum = mView.findViewById(R.id.MarketCapNum);
        BetaNum = mView.findViewById(R.id.BetaNum);
        PERationNum = mView.findViewById(R.id.PERationNum);
        EpsNum = mView.findViewById(R.id.EpsNum);
        EraningDateNum = mView.findViewById(R.id.EraningDateNum);
        mChart = mView.findViewById(R.id.chart1);
        currentPrice = mView.findViewById(R.id.currentPrice);
        currentPriceChange = mView.findViewById(R.id.currentPriceChange);
        marketSpinner = mView.findViewById(R.id.marketSpinner);
    }

    private void initial() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.market, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        marketSpinner.setAdapter(adapter);
    }

    private YahooRealtimeDataStruct loadSearchRecordPreferences() {
        Gson gson = new Gson();
        YahooRealtimeDataStruct searchRecord = new YahooRealtimeDataStruct();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String jsonPreferences = sharedPref.getString(MainActivity.SEARCHRECORD_SINGLERECORD, "");
        Type type = new TypeToken<YahooRealtimeDataStruct>() {
        }.getType();
        searchRecord = gson.fromJson(jsonPreferences, type);
        return searchRecord;
    }
    
    private String getRevisedStockNum(String input, String domain) {
        String inputStockNumber = input;
        switch (domain) {
            case "hsi":
                inputStockNumber = "%5EHSI";
            case "hk":
            default:
                int tempLength = inputStockNumber.length();
                if (inputStockNumber.length() > 0 && inputStockNumber.length() < 5) {
                    for (int i = 0; i < (4 - tempLength); i++) {
                        inputStockNumber = "0" + inputStockNumber;
                    }
                } else {
                    Log.e("error", "wrong stock quote");
                    inputStockNumber = "0005";
                }
                inputStockNumber += ".HK";
                break;
        }

        return inputStockNumber;
    }

    private void setUpMarketSpinner(){
        String[] stockHints_SZ = getResources().getStringArray(R.array.StockHints_SZ);
        String[] stockHints_NASDAQ = getResources().getStringArray(R.array.StockHints_NASDAQ);
        String[] stockHints_NYSE = getResources().getStringArray(R.array.StockHints_NYSE);
        String[] stockHints_HK = getResources().getStringArray(R.array.StockHints_HK);
        List<String> hintList = Arrays.asList(stockHints_HK);

        String selectedMarket = marketSpinner.getSelectedItem().toString();
        switch(selectedMarket) {
            case "NASDAQ":
                autoCompleteTextView.setInputType(1001); // textCapCharacters = 1001
                hintList = Arrays.asList(stockHints_NASDAQ);
                break;
            case "Shenzhen":
                autoCompleteTextView.setInputType(2); // // number = 2
                hintList = Arrays.asList(stockHints_SZ);
                break;
            case "NYSE":
                autoCompleteTextView.setInputType(1001); // textCapCharacters = 1001
                hintList = Arrays.asList(stockHints_NYSE);
                break;
            case "Hong Kong":
            default:
                autoCompleteTextView.setInputType(2); // // number = 2
                break;
        }
        stockHintAdapter = new StockHintAdapter(getActivity(), android.R.layout.simple_list_item_1, hintList);
        autoCompleteTextView.setThreshold(0);
        autoCompleteTextView.setDropDownWidth(350);
        autoCompleteTextView.setAdapter(stockHintAdapter);

    }

    private class GetYahooRealtimeData extends AsyncTask<String, Void, YahooRealtimeDataStruct> {
        ProgressDialog mYahooRealTimeDialog = new ProgressDialog(getActivity());

        private void updateMap(Elements rows, Map dataMap) {
            for (int i = 0; i < rows.size(); i++) { //first row is the col names so skip it.
                Element row = rows.get(i);
                Elements cols = row.select("td");

                dataMap.put(cols.get(0).text(), cols.get(1).text());
            }
        }

        private void generateRealTimeDataSet(YahooRealtimeDataStruct realTimeDataSet, Map dataMap) {

            // add from head
            realTimeDataSet.setCompanyName(dataMap.get("companyName").toString());
            realTimeDataSet.setStockSym(dataMap.get("stockSym").toString());
            realTimeDataSet.setCurrent(dataMap.get("stockQuote").toString());
            realTimeDataSet.setChange(dataMap.get("changes").toString());
            realTimeDataSet.setLastUpdatedTime(dataMap.get("updatedTime").toString());

            // add from table
            realTimeDataSet.setPreviousClose(dataMap.get("Previous Close").toString());
            realTimeDataSet.setOpen(dataMap.get("Open").toString());
            realTimeDataSet.setBid(dataMap.get("Bid").toString());
            realTimeDataSet.setAsk(dataMap.get("Ask").toString());
            realTimeDataSet.setDayRange(dataMap.get("Day's Range").toString());
            realTimeDataSet.setWeekRange52(dataMap.get("52 Week Range").toString());
            realTimeDataSet.setVolume(dataMap.get("Volume").toString());
            realTimeDataSet.setVolumeAvg(dataMap.get("Avg. Volume").toString());
            realTimeDataSet.setMarketCap(dataMap.get("Market Cap").toString());
            realTimeDataSet.setBeta(dataMap.get("Beta").toString());
            realTimeDataSet.setPeRatio(dataMap.get("PE Ratio (TTM)").toString());
            realTimeDataSet.setEps(dataMap.get("EPS (TTM)").toString());
            realTimeDataSet.setEarningsDate(dataMap.get("Earnings Date").toString());
            realTimeDataSet.setForwardDividend(dataMap.get("Forward Dividend & Yield").toString());
            realTimeDataSet.setExDividendData(dataMap.get("Ex-Dividend Date").toString());
            realTimeDataSet.setTargetEst1year(dataMap.get("1y Target Est").toString());

            //Sample: [
            // Previous Close 79.550
            // Open 79.900
            // Bid 79.650 x 0
            // Ask 79.700 x 0
            // Day's Range 79.600 - 79.950
            // 52 Week Range 61.800 - 86.000
            // Volume 23,199,947
            // Avg. Volume 33,440,139,
            // Market Cap 1.587T
            // Beta N/A
            // PE Ratio (TTM) 165.94
            // EPS (TTM) 0.480
            // Earnings Date N/A
            // Forward Dividend & Yield 3.99 (5.00%)
            // Ex-Dividend Date 2017-02-23
            // 1y Target Est 81.59]
        }

        private void getHeadInfo(Document doc, Map dataMap) {

            Element stockHead = doc.body().getElementsByClass("D(ib) Fz(18px)").first();
            String[] tokens = stockHead.text().split(" \\(");

            String companyName = tokens[0];
            dataMap.put("companyName", companyName);
            String stockSym = tokens[1].replaceAll("\\)", "");
            dataMap.put("stockSym", stockSym);

            Element stockBody = doc.body().getElementsByClass("D(ib) Mend(20px)").first();

            Elements stockQuoteTag = stockBody.getElementsByAttributeValue("data-reactid", String.valueOf(35));
            String stockQuote = stockQuoteTag.first().text();
            dataMap.put("stockQuote", stockQuote);

            Elements changesTag = stockBody.getElementsByAttributeValue("data-reactid", String.valueOf(37));
            String changes = changesTag.first().text();
            dataMap.put("changes", changes);

            Elements updatedTimeTage = stockBody.getElementsByAttributeValue("data-reactid", String.valueOf(40));
            String updatedTime = updatedTimeTage.first().text();
            dataMap.put("updatedTime", updatedTime);

        }

        private void getTableValues(Document doc, Map dataMap) {
            Element quoteSummary = doc.body().getElementById("quote-summary");
            Element table1 = quoteSummary.select("table").first();
            Element table2 = quoteSummary.select("table").get(1);

            updateMap(table1.select("tr"), dataMap);
            updateMap(table2.select("tr"), dataMap);

        }

        private JSONArray appendSearchRecordPreferences(YahooRealtimeDataStruct record) {
            // tutorial of sharedpreference: https://stackoverflow.com/questions/28107647/how-to-save-listobject-to-sharedpreferences

            Gson gson = new Gson();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String jsonSaved = sharedPref.getString(MainActivity.SEARCHRECORD, "");
            String jsonNewRecordToAdd = gson.toJson(record);

            JSONArray jsonArrayStockRecordList = new JSONArray();

            try {
                if (jsonSaved.compareTo("") != 0) {
                    Type type = new TypeToken<List<YahooRealtimeDataStruct>>() {}.getType();
                    List<YahooRealtimeDataStruct> searchRecordList = gson.fromJson(jsonSaved, type);
                    for (int i = 0; searchRecordList != null && i < searchRecordList.size(); i++) {
                        if (searchRecordList.get(i).getStockSym().compareTo(record.getStockSym()) == 0){
                            searchRecordList.remove(i);
                            break;
                        }
                    }
                    jsonArrayStockRecordList = new JSONArray(gson.toJson(searchRecordList));
                }
                jsonArrayStockRecordList.put(new JSONObject(jsonNewRecordToAdd));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //SAVE NEW ARRAY
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(MainActivity.SEARCHRECORD, String.valueOf(jsonArrayStockRecordList));
            editor.commit();
            editor.apply();

            return jsonArrayStockRecordList;
        }

        private void assignDataToView(YahooRealtimeDataStruct stock) {
            QuoteNum.setText(String.valueOf(stock.getStockSym()));
            QuoteName.setText(String.valueOf(stock.getCompanyName()));
            LastUpdatedTime.setText(String.valueOf(stock.getLastUpdatedTime()));

            PreviousCloseNum.setText(String.valueOf(stock.getPreviousClose()));
            OpenNum.setText(String.valueOf(stock.getOpen()));
            HighNum.setText(String.valueOf(stock.getHigh()));
            LowNum.setText(String.valueOf(stock.getLow()));
            VolumnNum.setText(String.valueOf(stock.getVolume()));

            BidNum.setText(String.valueOf(stock.getBid()));
            AskNum.setText(String.valueOf(stock.getAsk()));
            DayRangeNum.setText(String.valueOf(stock.getDayRange()));
            WeekRangeNum.setText(String.valueOf(stock.getWeekRange52()));
            AvgVolumnNum.setText(String.valueOf(stock.getVolumeAvg()));

            MarketCapNum.setText(String.valueOf(stock.getMarketCap()));
            BetaNum.setText(String.valueOf(stock.getBeta()));
            PERationNum.setText(String.valueOf(stock.getPeRatio()));
            EpsNum.setText(String.valueOf(stock.getEps()));
            EraningDateNum.setText(String.valueOf(stock.getEarningsDate()));

            currentPrice.setText(String.valueOf(stock.getCurrent()));
            currentPriceChange.setText(String.valueOf(stock.getChange()));

            char changeSign = String.valueOf(stock.getChange()).charAt(0);

            if (changeSign == '+') {
                currentPriceChange.setTextColor(GREEN);
            }else if (changeSign == '-'){
                currentPriceChange.setTextColor(RED);
            }else{
                currentPriceChange.setTextColor(BLACK);
            }
        }

        private void saveLatestSearchRecord(YahooRealtimeDataStruct record){
            Gson gson = new Gson();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String jsonNewRecordToAdd = gson.toJson(record);

            JSONObject jsonArrayStockRecordList = null;
            try {
                jsonArrayStockRecordList = new JSONObject(jsonNewRecordToAdd);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("jsonObjectRecordList", String.valueOf(jsonArrayStockRecordList));
            //SAVE NEW ARRAY
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(MainActivity.SEARCHRECORD_SINGLERECORD, String.valueOf(jsonArrayStockRecordList));
            editor.commit();
            editor.apply();
        }

        protected YahooRealtimeDataStruct doInBackground(String... params) {

            YahooRealtimeDataStruct realTimeDataSet = new YahooRealtimeDataStruct();
            Map dataMap = new HashMap();

            String inputStockNumber = params[0];

            try {
                String tempUrl = "https://finance.yahoo.com/quote/" + inputStockNumber + "?p=" + inputStockNumber;
                Log.e("html", "fetching " + tempUrl);

                Document doc = Jsoup.connect(tempUrl).get();

                getHeadInfo(doc, dataMap);
                getTableValues(doc, dataMap);

                generateRealTimeDataSet(realTimeDataSet, dataMap);

                appendSearchRecordPreferences(realTimeDataSet);
                saveLatestSearchRecord(realTimeDataSet);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }

            return realTimeDataSet;
        }

        @Override
        protected void onPostExecute(YahooRealtimeDataStruct result) {
            super.onPostExecute(result);
            mYahooRealTimeDialog.dismiss();
            mOutputText.setText(result.toString());
            autoCompleteTextView.setText("");
            Log.e("endtask", result.toString());
            assignDataToView(result);
        }

        @Override
        protected void onPreExecute() {
            mYahooRealTimeDialog.setMessage("Downloading Yahoo Financial Data ...");
            if (!mYahooRealTimeDialog.isShowing())
                mYahooRealTimeDialog.show();
        }

        @Override
        protected void onCancelled() {
            mYahooRealTimeDialog.dismiss();
        }
    }

    private class GetHistoricalDataByJson extends AsyncTask<String, Void, Void> {
        List<Quote> quotes;


        private String getJSONData(String tempUrl) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String result = "";
            try {
                URL url = new URL(tempUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                result = buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        private void generateQuotes(String rawData) {
            quotes = new ArrayList<>();

            try {
                JSONObject parentObject = new JSONObject(rawData);
                JSONObject chart = parentObject.getJSONObject("chart");
                JSONObject result = chart.getJSONArray("result").getJSONObject(0);
                Log.e("result", result.toString());

                JSONArray timeStamp = result.getJSONArray("timestamp");
                Log.e("timeStamp", timeStamp.toString());

                JSONObject indicators = result.getJSONObject("indicators");
                JSONObject quote = indicators.getJSONArray("quote").getJSONObject(0);

                JSONArray volume = quote.getJSONArray("volume");
                Log.e("volume", volume.toString());

                JSONArray high = quote.getJSONArray("high");
                Log.e("high", high.toString());

                JSONArray close = quote.getJSONArray("close");
                Log.e("close", close.toString());

                JSONArray open = quote.getJSONArray("open");
                Log.e("open", open.toString());

                JSONArray low = quote.getJSONArray("low");
                Log.e("low", low.toString());

                for (int i = 0; i < timeStamp.length(); i++) {
                    Quote tempQuote = new Quote();
                    if (!timeStamp.isNull(i)) {
                        tempQuote.timeStamp = timeStamp.getLong(i);
                    } else {
                        tempQuote.timeStamp = 0;
                    }
                    if (!volume.isNull(i)) {
                        tempQuote.volume = volume.getInt(i);
                    } else {
                        tempQuote.volume = 0;
                    }

                    if (!high.isNull(i)) {
                        tempQuote.high = high.getDouble(i);
                    } else {
                        tempQuote.high = 0;
                    }
                    if (!close.isNull(i)) {
                        tempQuote.close = close.getDouble(i);
                    } else {
                        tempQuote.close = 0;
                    }

                    if (!open.isNull(i)) {
                        tempQuote.open = open.getDouble(i);
                    } else {
                        tempQuote.open = 0;
                    }
                    if (!low.isNull(i)) {
                        tempQuote.low = low.getDouble(i);
                    } else {
                        tempQuote.low = 0;
                    }

                    quotes.add(tempQuote);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                List<Quote> tempQuotes = new ArrayList<>();
                for (int i = 0; i < quotes.size(); i++) {
                    boolean highIsNull = quotes.get(i).high == 0;
                    boolean openIsNull = quotes.get(i).open == 0;
                    boolean closeIsNull = quotes.get(i).close == 0;
                    boolean lowIsNull = quotes.get(i).low == 0;
                    if (!highIsNull && !openIsNull &&
                            !closeIsNull && !lowIsNull)
                        tempQuotes.add(quotes.get(i));
                }
                quotes.clear();
                quotes.addAll(tempQuotes);
                tempQuotes = null;
            }
        }

        private void setupCandleStickChart() {

            mChart.resetTracking();

            ArrayList<CandleEntry> yVals1 = new ArrayList<CandleEntry>();
            ArrayList<String> labels = new ArrayList<>();
            for (int i = 0; i < quotes.size(); i++) {

                long date = quotes.get(i).timeStamp;

                float high = (float) quotes.get(i).high;
                float low = (float) quotes.get(i).low;

                float open = (float) quotes.get(i).open;
                float close = (float) quotes.get(i).close;

                yVals1.add(new CandleEntry(
                        i, high,
                        low,
                        open,
                        close
                ));
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(date * 1000L);
                String dateString = DateFormat.format("HH:mm", cal).toString();
                labels.add(dateString);
            }

            CandleDataSet set1 = new CandleDataSet(yVals1, "Data Set");

            set1.setDrawIcons(false);
//        set1.setAxisDependency(AxisDependency.LEFT);
//        set1.setColor(Color.rgb(80, 80, 80));
            set1.setShadowColor(Color.DKGRAY);
            set1.setShadowWidth(0.7f);
            set1.setDecreasingColor(Color.RED);
            set1.setDecreasingPaintStyle(Paint.Style.FILL);
            set1.setIncreasingColor(Color.rgb(122, 242, 84));
            set1.setIncreasingPaintStyle(Paint.Style.FILL_AND_STROKE);
            set1.setNeutralColor(Color.BLUE);
            //set1.setHighlightLineWidth(1f);
            mChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
            mChart.getXAxis().setGranularity(1f);
            mChart.getXAxis().setLabelRotationAngle(40f);
            CandleData data = new CandleData(set1);

            mChart.setData(data);
            mChart.invalidate();
        }

        private void saveLatestSearchHistoricalRecord(){
            Gson gson = new Gson();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String jsonNewRecordToAdd = gson.toJson(quotes);

            JSONArray jsonArrayStockRecordList = null;
            try {
                jsonArrayStockRecordList = new JSONArray(jsonNewRecordToAdd);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("jsonHisRecordList", String.valueOf(jsonArrayStockRecordList));
            //SAVE NEW ARRAY
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(MainActivity.SEARCHRECORD_SINGLEHISTORICALRECORD, String.valueOf(jsonArrayStockRecordList));
            editor.commit();
            editor.apply();
        }

        @Override
        protected Void doInBackground(String... strings) {
            Log.e("yahooData", "params = " + strings[0]);

            String inputStockNumber = strings[0];
            Log.e("params", inputStockNumber);

            String tempUrl = "https://query1.finance.yahoo.com/v7/finance/chart/" + inputStockNumber + "?range=1d&interval=60m&indicators=quote&includeTimestamps=true";
            Log.e("searchingURL", "url: " + tempUrl);

            String rawData = getJSONData(tempUrl);
            Log.e("raw", rawData);

            generateQuotes(rawData);
            Log.e("quotes", quotes.toString());

            saveLatestSearchHistoricalRecord();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mYahooRealTimeDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setupCandleStickChart();
            //mYahooRealTimeDialog.hide();
        }

    }

    public class Quote {
        long timeStamp;
        long volume;
        double high;
        double close;
        double open;
        double low;

        @Override
        public String toString() {
            return "Quote{" +
                    "timeStamp=" + timeStamp +
                    ", volume=" + volume +
                    ", high=" + high +
                    ", close=" + close +
                    ", open=" + open +
                    ", low=" + low +
                    '}';
        }
    }

    public class StockHintAdapter extends ArrayAdapter implements Filterable {

        List<String> allCodes;
        List<String> originalCodes;
        StringFilter filter;

        public StockHintAdapter(Context context, int resource, List<String> keys) {
            super(context, resource, keys);
            allCodes = keys;
            originalCodes = keys;
        }

        public int getCount() {
            if (allCodes == null) {
                return 0;
            }
            return allCodes.size();
        }

        public Object getItem(int position) {
            return allCodes.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        private class StringFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = "";
                if (constraint != null) {
                    filterString = constraint.toString().toLowerCase();
                }

                FilterResults results = new FilterResults();
                final List<String> list = originalCodes;

                int count = list.size();
                final ArrayList<String> nlist = new ArrayList<String>(count);
                String filterableString;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i);
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(filterableString);
                    }
                }

                String a = getRevisedStockNum(filterString, "hk");
                for (int i = 0; i < nlist.size(); i++) {
                    if (nlist.get(i).compareTo(a) == 0) {
                        nlist.remove(i);
                        nlist.add(0, a);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                allCodes = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }
        }

        @Override
        public Filter getFilter() {
            return new StringFilter();
        }
    }

}
