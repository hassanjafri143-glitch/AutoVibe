# AutoVibe — Lahore On-Demand Mechanic Marketplace

An interactive, InDrive-inspired Android marketplace prototype built for **Lahore, Pakistan**, connecting vehicle owners with nearby CNIC-verified car and motorcycle mechanics through real-time PKR fare bidding and live map tracking.

---

## 📱 Download & Install on Android (Direct APK Link)

> **Repo Owner — 1-Time Setup (30 Seconds):**
> 1. In Google AI Studio, open **Settings → Generate APK** to download your `.apk` file and rename it to `AutoVibe.apk`.
> 2. On GitHub, open **Releases → Draft a new release**, enter tag `v1.0`, attach `AutoVibe.apk`, and click **Publish release**.
> 3. Replace `hassanjafri143-glitch/AutoVibe` in the link below with your GitHub username and repository name (or paste the exact asset URL from your Release).

### 👉 Direct APK Download (Tap on Android Phone)
**[Download AutoVibe.apk (Latest Release)](https://github.com/hassanjafri143-glitch/AutoVibe/releases/latest/download/AutoVibe.apk)**

*Compatibility: Android 7.0 (API 24) and higher • Package ID: `com.aistudio.autovibe.lhrpkt`*

---

### How to Install on Your Phone (For Evaluators & Testers)

1. **Open the Download Link** above directly in Chrome (or your phone's default browser) to download `AutoVibe.apk`.
2. **Open the Downloaded File** from your browser downloads bar or notification shade.
3. **Allow Unknown Apps (One-Time Prompt):**
   * If Android asks *"For your security, your phone currently isn't allowed to install unknown apps from this source"*, tap **Settings → toggle "Allow from this source" ON**, then go back and tap **Install**.
4. **Google Play Protect Prompt (If Shown):**
   * Because this app is distributed directly outside the Google Play Store, Play Protect may show *"Unrecognized developer"* or *"Unsafe app blocked"*. Tap **More details → Install anyway** to finish installing.
5. **Launch AutoVibe:** Tap **Open** — no account setup or Play Store sign-in is required to explore the prototype.

---

## 🌐 Instant Browser Preview (No Install Needed)
Want to test the app immediately without installing an APK? Open the live streaming preview:
* **[Open AutoVibe Interactive Preview](https://ais-pre-letgvvbyxv4uoi75ey2v7b-688924818921.asia-east1.run.app)**

---

## ✨ Key Prototype Features
* **Role Switcher (`Customer` ↔ `Mechanic`):** Toggle live between both sides of the Lahore marketplace from the top bar.
* **Lahore Interactive Dark Map:** Covers Gulberg III, DHA Phase 5, Johar Town, Model Town, Township, Mall Road, Cavalry Ground, and Wapda Town with pulsing location pins, patrolling mechanics, and animated route tracking.
* **Customer 4-Step Bidding Flow:**
  1. **Vehicle Selection:** Car vs. Motorcycle with Pakistani market cascading dropdowns (Toyota, Honda, Suzuki, KIA, Hyundai, Changan, Yamaha, United, Road Prince).
  2. **Issue & Photo Upload:** Quick-select issue tags, custom notes, and zero-permission Android Photo Picker upload.
  3. **PKR Fare Bidding:** Propose a call-out fare in PKR and compare nearby mechanic offers/counter-offers with Accept & Decline controls.
  4. **Live Tracking:** Animated mechanic route toward your Lahore pin, CNIC-verified card, Call action, and completion rating.
* **Mechanic Mode Dashboard:** Online/Offline switch, CNIC verification (`XXXXX-XXXXXXX-X`) with **Verified Mechanic** badge, workshop photo preview, and live feed of Lahore repair requests with **Accept** and **Counter-Offer** actions.
* **Local Prototype Auth:** Customer Sign In / Sign Up (`+92 3XX XXXXXXX` + simulated SMS OTP) and Mechanic CNIC onboarding stored locally on-device.

---

## 🔒 Security & Permissions Summary
* **Zero sensitive permissions:** Requests no background location, contacts, or broad storage access (`READ_MEDIA_*` / `READ_EXTERNAL_STORAGE` are not used; image selection uses Android's system Photo Picker).
* **No embedded API keys:** All prototype data, map rendering, and local auth run self-contained inside the APK.
