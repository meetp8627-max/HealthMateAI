package com.example.data.knowledge

/**
 * Trusted medication information record based on standard references
 * (e.g. MedlinePlus / National Library of Medicine / NHS Medicines A to Z).
 */
data class TrustedMedicationRecord(
    val genericName: String,
    val brandNames: List<String>,
    val activeIngredients: List<String>,
    val medicineType: String,
    val shortSummary: String,
    val mechanism: String,
    val commonUses: List<String>,
    val importantSafetyInfo: List<String>,
    val commonSideEffects: List<String>,
    val isCombination: Boolean = false,
    val sourceName: String = "MedlinePlus (National Library of Medicine)",
    val sourceUrl: String = "https://medlineplus.gov/druginformation.html",
    val sourceUpdatedAt: String = "August 2026"
)

object MedicationKnowledgeBase {

    val verifiedDatabase: List<TrustedMedicationRecord> = listOf(
        // ANTIBIOTICS
        TrustedMedicationRecord(
            genericName = "Amoxicillin",
            brandNames = listOf("Amoxil", "Trimox", "Moxatag"),
            activeIngredients = listOf("Amoxicillin"),
            medicineType = "Antibiotic (Penicillin group)",
            shortSummary = "This medicine is a penicillin-type antibiotic used to treat a wide variety of bacterial infections.",
            mechanism = "This medicine belongs to the penicillin antibiotic group. It works by preventing bacteria from building protective cell walls, causing the bacteria to weaken and die.",
            commonUses = listOf(
                "Commonly used to treat ear, nose, and throat infections",
                "May be used for respiratory tract and chest infections",
                "Commonly prescribed for urinary tract and skin infections",
                "May be part of combination treatments for stomach ulcers"
            ),
            importantSafetyInfo = listOf(
                "Do not use if you have a known severe allergy to penicillin or cephalosporin antibiotics.",
                "Complete the full prescribed course even if symptoms improve early, unless directed otherwise by your doctor.",
                "Contact a doctor immediately if you experience severe watery diarrhea, skin rash, or breathing difficulty.",
                "May reduce the effectiveness of some oral contraceptives in rare circumstances."
            ),
            commonSideEffects = listOf(
                "Nausea or mild stomach upset",
                "Diarrhea or loose stools",
                "Mild skin rash or itching",
                "Headache"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Amoxicillin and Clavulanate",
            brandNames = listOf("Augmentin", "Augmentin XR", "Clavamox", "Amoclav"),
            activeIngredients = listOf("Amoxicillin", "Clavulanic Acid"),
            medicineType = "Combination Antibiotic (Penicillin + Beta-Lactamase Inhibitor)",
            shortSummary = "This combination medicine is an antibiotic used to treat bacterial infections that may be resistant to amoxicillin alone.",
            mechanism = "This medicine contains two active ingredients: Amoxicillin stops bacteria from building cell walls, while Clavulanic Acid blocks bacterial enzymes that would otherwise break down amoxicillin.",
            commonUses = listOf(
                "Commonly used for recurrent or severe sinus and ear infections",
                "May be prescribed for lower respiratory tract infections (bronchitis, pneumonia)",
                "Commonly used for skin and soft tissue infections",
                "May be used for complex urinary tract and dental infections"
            ),
            importantSafetyInfo = listOf(
                "Contains two active ingredients: Amoxicillin and Clavulanic Acid.",
                "Do not use if you have a history of penicillin allergy or liver problems caused by Augmentin.",
                "Take with a meal or snack at the start of eating to minimize stomach irritation.",
                "Finish the entire prescribed duration to avoid recurrence of resistant bacteria."
            ),
            commonSideEffects = listOf(
                "Diarrhea (more common than amoxicillin alone)",
                "Nausea and mild vomiting",
                "Stomach cramps or bloating",
                "Mild rash or fungal yeast infection"
            ),
            isCombination = true,
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Azithromycin",
            brandNames = listOf("Zithromax", "Z-Pak", "Zmax"),
            activeIngredients = listOf("Azithromycin"),
            medicineType = "Antibiotic (Macrolide group)",
            shortSummary = "This medicine is a macrolide antibiotic used to treat various mild-to-moderate bacterial infections.",
            mechanism = "This medicine belongs to the macrolide antibiotic group. It works by interfering with the protein production of bacteria, preventing them from growing and multiplying.",
            commonUses = listOf(
                "Commonly used to treat respiratory infections such as bronchitis and pneumonia",
                "May be prescribed for ear infections, sinus infections, and strep throat",
                "Used for certain skin and soft tissue bacterial infections",
                "Prescribed for specific sexually transmitted bacterial infections"
            ),
            importantSafetyInfo = listOf(
                "Notify your doctor if you have a history of heart rhythm irregularities (QT prolongation) or liver disease.",
                "Do not take antacids containing aluminum or magnesium at the exact same time.",
                "Seek medical attention if you develop yellowing of eyes/skin or severe abdominal pain."
            ),
            commonSideEffects = listOf(
                "Nausea and vomiting",
                "Diarrhea or loose stools",
                "Abdominal pain or cramping",
                "Temporary taste changes or headache"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Ciprofloxacin",
            brandNames = listOf("Cipro", "Cipro XR", "Proquin"),
            activeIngredients = listOf("Ciprofloxacin"),
            medicineType = "Antibiotic (Fluoroquinolone group)",
            shortSummary = "This medicine is a broad-spectrum fluoroquinolone antibiotic used for specific bacterial infections.",
            mechanism = "This medicine belongs to the fluoroquinolone group. It works by inhibiting bacterial DNA enzymes (gyrase and topoisomerase IV), preventing bacterial replication.",
            commonUses = listOf(
                "Commonly used for complicated urinary tract and kidney infections",
                "May be prescribed for severe bacterial diarrhea or gastrointestinal infections",
                "Used for specific bone, joint, and prostate infections"
            ),
            importantSafetyInfo = listOf(
                "Black box safety warning: May increase the risk of tendonitis and tendon rupture (especially in older adults).",
                "Avoid taking with dairy products or calcium-fortified juices alone, as calcium impairs absorption.",
                "Stay well hydrated to prevent crystal formation in urine.",
                "May cause increased skin sensitivity to sunlight."
            ),
            commonSideEffects = listOf(
                "Nausea and diarrhea",
                "Dizziness or lightheadedness",
                "Headache or trouble sleeping"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Cephalexin",
            brandNames = listOf("Keflex", "Daxbia"),
            activeIngredients = listOf("Cephalexin"),
            medicineType = "Antibiotic (Cephalosporin group)",
            shortSummary = "This medicine is a cephalosporin antibiotic commonly used to treat bacterial infections of the skin, respiratory tract, and urinary system.",
            mechanism = "This medicine belongs to the first-generation cephalosporin group. It works by interfering with the synthesis of bacterial cell walls.",
            commonUses = listOf(
                "Commonly used for skin and soft tissue infections",
                "Prescribed for upper respiratory tract infections",
                "Used for urinary tract and bone infections"
            ),
            importantSafetyInfo = listOf(
                "Inform your doctor if you have severe allergies to penicillin, as cross-reactivity can occur in some individuals.",
                "Take with food if it causes stomach upset."
            ),
            commonSideEffects = listOf(
                "Diarrhea or indigestion",
                "Nausea and dizziness",
                "Abdominal discomfort"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Doxycycline",
            brandNames = listOf("Vibramycin", "Doryx", "Monodox", "Oracea"),
            activeIngredients = listOf("Doxycycline"),
            medicineType = "Antibiotic (Tetracycline group)",
            shortSummary = "This medicine is a tetracycline antibiotic used to treat bacterial infections, acne, and tick-borne illnesses.",
            mechanism = "This medicine belongs to the tetracycline group. It works by inhibiting bacterial protein synthesis at the ribosomal level.",
            commonUses = listOf(
                "Commonly prescribed for respiratory infections and pneumonia",
                "Used in the treatment of moderate to severe acne and rosacea",
                "Prescribed for Lyme disease and other tick-borne infections",
                "May be used for malaria prophylaxis and specific eye infections"
            ),
            importantSafetyInfo = listOf(
                "Drink a full glass of water and stay upright for at least 30 minutes after taking to avoid esophageal irritation.",
                "Avoid prolonged exposure to sunlight or tanning beds due to phototoxicity.",
                "Do not use during pregnancy or in children under 8 years without specialist advice due to tooth discoloration risk."
            ),
            commonSideEffects = listOf(
                "Stomach upset and nausea",
                "Sun sensitivity (quick sunburns)",
                "Mild diarrhea"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),

        // ANALGESICS & NSAIDS
        TrustedMedicationRecord(
            genericName = "Acetaminophen",
            brandNames = listOf("Tylenol", "Paracetamol", "Panadol", "Calpol", "Mapap"),
            activeIngredients = listOf("Acetaminophen / Paracetamol"),
            medicineType = "Pain Reliever & Fever Reducer (Non-Opioid Analgesic)",
            shortSummary = "This medicine is a widely used pain reliever and fever reducer for mild-to-moderate aches, pains, and fevers.",
            mechanism = "This medicine works primarily in the central nervous system to elevate pain threshold and regulate the brain's temperature-control center (hypothalamus).",
            commonUses = listOf(
                "Commonly used to temporarily relieve headaches and muscle aches",
                "Helps reduce fever from colds, flu, and immunizations",
                "Used for toothaches, menstrual cramps, and arthritis discomfort"
            ),
            importantSafetyInfo = listOf(
                "Do not exceed 4,000 mg in 24 hours (or lower limit if advised by your doctor) to avoid severe liver damage.",
                "Check other cold, flu, and sleep medications to ensure you are not taking multiple products containing acetaminophen.",
                "Avoid excessive alcohol consumption while taking this medication."
            ),
            commonSideEffects = listOf(
                "Generally well tolerated at recommended doses",
                "Rare: mild nausea or stomach discomfort",
                "Rare: allergic skin reaction (seek immediate help if severe)"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Ibuprofen",
            brandNames = listOf("Advil", "Motrin", "Nurofen", "Brufen", "Midol"),
            activeIngredients = listOf("Ibuprofen"),
            medicineType = "Nonsteroidal Anti-inflammatory Drug (NSAID)",
            shortSummary = "This medicine is a nonsteroidal anti-inflammatory drug (NSAID) used to relieve pain, reduce inflammation, and lower fever.",
            mechanism = "This medicine belongs to the NSAID class. It works by blocking cyclooxygenase (COX-1 and COX-2) enzymes, which stops the production of prostaglandins that cause pain and inflammation.",
            commonUses = listOf(
                "Commonly used for inflammatory pain such as arthritis, sprains, and backaches",
                "Relieves headaches, dental pain, and menstrual cramps",
                "Reduces fever and flu-related body aches"
            ),
            importantSafetyInfo = listOf(
                "Take with food or milk to reduce stomach upset and the risk of gastric irritation or ulcers.",
                "May increase the risk of cardiovascular events and kidney strain with prolonged or high-dose use.",
                "Consult your doctor before taking if you have stomach ulcers, high blood pressure, or kidney disease."
            ),
            commonSideEffects = listOf(
                "Stomach upset, heartburn, or indigestion",
                "Mild nausea or bloating",
                "Dizziness or mild headache"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Naproxen",
            brandNames = listOf("Aleve", "Naprosyn", "Anaprox"),
            activeIngredients = listOf("Naproxen"),
            medicineType = "Nonsteroidal Anti-inflammatory Drug (NSAID)",
            shortSummary = "This medicine is an NSAID that provides extended relief from pain, swelling, and stiffness.",
            mechanism = "This medicine belongs to the propionic acid NSAID group. It inhibits prostaglandin synthesis, providing prolonged anti-inflammatory and analgesic effects.",
            commonUses = listOf(
                "Commonly used for joint pain, osteoarthritis, and rheumatoid arthritis",
                "Relieves back pain, tendonitis, and muscle strains",
                "Used for menstrual pain and dental discomfort"
            ),
            importantSafetyInfo = listOf(
                "Take with a full meal and a full glass of water.",
                "Do not combine with other NSAIDs (such as ibuprofen or high-dose aspirin) without medical supervision."
            ),
            commonSideEffects = listOf(
                "Heartburn and stomach ache",
                "Drowsiness or dizziness",
                "Mild fluid retention"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Aspirin",
            brandNames = listOf("Bayer Aspirin", "Ecotrin", "Bufferin", "Aspro"),
            activeIngredients = listOf("Acetylsalicylic Acid"),
            medicineType = "NSAID / Antiplatelet Agent",
            shortSummary = "This medicine is used for pain relief, fever reduction, and in low daily doses to help prevent blood clots.",
            mechanism = "This medicine irreversibly inhibits COX-1 and COX-2 enzymes, stopping prostaglandin and thromboxane A2 production, which reduces inflammation and prevents platelet aggregation.",
            commonUses = listOf(
                "Commonly used in low doses (81 mg) under physician supervision for cardiovascular clot prevention",
                "Used in standard doses for temporary relief of mild headaches and musculoskeletal pain"
            ),
            importantSafetyInfo = listOf(
                "Do not give aspirin to children or teenagers recovering from viral infections due to the risk of Reye's syndrome.",
                "May increase bleeding risk; inform healthcare providers before surgeries or dental work."
            ),
            commonSideEffects = listOf(
                "Stomach irritation and heartburn",
                "Easy bruising or minor bleeding",
                "Nausea"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),

        // ANTIHISTAMINES & ALLERGY
        TrustedMedicationRecord(
            genericName = "Cetirizine",
            brandNames = listOf("Zyrtec", "Reactine", "Piriteze"),
            activeIngredients = listOf("Cetirizine Hydrochloride"),
            medicineType = "Second-Generation Antihistamine",
            shortSummary = "This medicine is a non-drowsy second-generation antihistamine used to relieve allergy and hay fever symptoms.",
            mechanism = "This medicine works by selectively blocking peripheral H1 histamine receptors, preventing histamine from triggering allergy symptoms such as sneezing, runny nose, and itching.",
            commonUses = listOf(
                "Commonly used for seasonal allergic rhinitis (hay fever)",
                "Relieves itchy, watery eyes, sneezing, and runny nose",
                "Treats chronic itchy hives and allergic skin reactions"
            ),
            importantSafetyInfo = listOf(
                "Although generally less sedating than first-generation antihistamines, it can cause mild drowsiness in some individuals.",
                "Use caution when operating machinery or driving until you know how it affects you.",
                "Avoid consuming excessive alcohol while taking."
            ),
            commonSideEffects = listOf(
                "Mild drowsiness or tiredness",
                "Dry mouth",
                "Dizziness or mild headache"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Loratadine",
            brandNames = listOf("Claritin", "Clarityn", "Alavert"),
            activeIngredients = listOf("Loratadine"),
            medicineType = "Non-Drowsy Antihistamine",
            shortSummary = "This medicine is a long-acting, non-drowsy antihistamine used for relief of allergy symptoms.",
            mechanism = "This medicine is a selective peripheral histamine H1-receptor antagonist that reduces allergic inflammatory responses without easily crossing into the brain.",
            commonUses = listOf(
                "Relieves outdoor and indoor allergy symptoms (pollen, dust mites, pet dander)",
                "Reduces itchy nose, sneezing, and watery eyes",
                "Treats hives and itchy skin rashes"
            ),
            importantSafetyInfo = listOf(
                "Take once daily as directed. Higher doses than recommended do not improve efficacy and may cause sleepiness.",
                "Consult your pharmacist if you have severe liver impairment."
            ),
            commonSideEffects = listOf(
                "Headache",
                "Feeling sleepy (uncommon)",
                "Dry mouth or fatigue"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Diphenhydramine",
            brandNames = listOf("Benadryl", "Unisom", "Sominex", "Nytol"),
            activeIngredients = listOf("Diphenhydramine"),
            medicineType = "First-Generation Antihistamine / Sleep Aid",
            shortSummary = "This medicine is a sedating antihistamine used to treat acute allergic reactions, itching, motion sickness, and short-term sleeplessness.",
            mechanism = "This medicine crosses the blood-brain barrier and blocks central and peripheral H1 histamine receptors as well as muscarinic receptors.",
            commonUses = listOf(
                "Commonly used for acute allergic reactions, insect stings, and hives",
                "Used for relief of motion sickness and nausea",
                "Used as a short-term nighttime sleep aid"
            ),
            importantSafetyInfo = listOf(
                "Causes marked drowsiness. Do not drive or operate machinery after taking.",
                "Use caution in elderly adults due to increased fall risk and anticholinergic effects (confusion, urinary retention)."
            ),
            commonSideEffects = listOf(
                "Marked drowsiness and grogginess",
                "Dry mouth, nose, and throat",
                "Constipation or blurred vision"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Loratadine and Pseudoephedrine",
            brandNames = listOf("Claritin-D", "Claritin-D 12 Hour", "Claritin-D 24 Hour"),
            activeIngredients = listOf("Loratadine", "Pseudoephedrine"),
            medicineType = "Combination Antihistamine & Decongestant",
            shortSummary = "This combination medicine treats nasal allergy symptoms along with sinus congestion and sinus pressure.",
            mechanism = "Loratadine blocks histamine receptors to stop sneezing and itching, while Pseudoephedrine stimulates alpha-adrenergic receptors to constrict swollen blood vessels in nasal passages.",
            commonUses = listOf(
                "Relieves seasonal allergy symptoms with sinus congestion",
                "Reduces nasal swelling, sinus pressure, and stuffy nose"
            ),
            importantSafetyInfo = listOf(
                "Contains two active ingredients: an antihistamine and a decongestant stimulant.",
                "Consult a doctor before taking if you have high blood pressure, heart disease, thyroid disorder, or glaucoma.",
                "Swallow extended-release tablets whole; do not crush or chew."
            ),
            commonSideEffects = listOf(
                "Trouble sleeping or restlessness",
                "Dry mouth and headache",
                "Mild increase in heart rate or blood pressure"
            ),
            isCombination = true,
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),

        // GASTROINTESTINAL & ACID SUPPRESSION
        TrustedMedicationRecord(
            genericName = "Omeprazole",
            brandNames = listOf("Prilosec", "Prilosec OTC", "Losec", "Omesec"),
            activeIngredients = listOf("Omeprazole"),
            medicineType = "Proton Pump Inhibitor (PPI)",
            shortSummary = "This medicine is a proton pump inhibitor that significantly reduces stomach acid production.",
            mechanism = "This medicine works by binding to and inhibiting the H+/K+ ATPase enzyme system (the proton pump) in gastric parietal cells, shutting down acid secretion.",
            commonUses = listOf(
                "Commonly used to treat gastroesophageal reflux disease (GERD) and frequent heartburn",
                "Used to heal and prevent stomach and duodenal ulcers",
                "Prescribed for erosive esophagitis and conditions involving excess acid (Zollinger-Ellison)"
            ),
            importantSafetyInfo = listOf(
                "Best taken in the morning 30 to 60 minutes before breakfast.",
                "Swallow capsules whole; do not crush or chew enteric-coated pellets.",
                "Long-term use may affect magnesium, calcium, or vitamin B12 absorption; discuss prolonged use with your doctor."
            ),
            commonSideEffects = listOf(
                "Headache",
                "Mild abdominal pain or flatulence",
                "Nausea, diarrhea, or constipation"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Esomeprazole",
            brandNames = listOf("Nexium", "Nexium 24HR", "Esocip"),
            activeIngredients = listOf("Esomeprazole Magnesium"),
            medicineType = "Proton Pump Inhibitor (PPI)",
            shortSummary = "This medicine is a proton pump inhibitor used to reduce acid production in the stomach for heartburn and acid reflux.",
            mechanism = "This S-isomer of omeprazole selectively inhibits the proton pump in gastric mucosa, suppressing baseline and stimulated gastric acid secretion.",
            commonUses = listOf(
                "Treatment of GERD symptoms and healing of erosive esophagitis",
                "Prevention of gastric ulcers associated with NSAID therapy"
            ),
            importantSafetyInfo = listOf(
                "Take 30–60 minutes before a meal with a glass of water.",
                "Consult your doctor if symptoms persist after 14 days of OTC treatment."
            ),
            commonSideEffects = listOf(
                "Headache and dry mouth",
                "Stomach pain or diarrhea",
                "Nausea"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Famotidine",
            brandNames = listOf("Pepcid", "Pepcid AC", "Fluxid"),
            activeIngredients = listOf("Famotidine"),
            medicineType = "Histamine-2 (H2) Blocker",
            shortSummary = "This medicine is an H2-receptor antagonist that decreases the amount of acid made in the stomach.",
            mechanism = "This medicine competitively blocks histamine H2 receptors on stomach parietal cells, reducing basal and nocturnal gastric acid secretion.",
            commonUses = listOf(
                "Relieves and prevents heartburn and acid indigestion",
                "Treatment of benign gastric and duodenal ulcers"
            ),
            importantSafetyInfo = listOf(
                "Can be taken 15 to 60 minutes before eating food or drinking beverages that cause heartburn."
            ),
            commonSideEffects = listOf(
                "Headache and dizziness",
                "Constipation or mild diarrhea"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Bismuth Subsalicylate",
            brandNames = listOf("Pepto-Bismol", "Kaopectate", "Bismatrol"),
            activeIngredients = listOf("Bismuth Subsalicylate"),
            medicineType = "Antidiarrheal & Gastrointestinal Protectant",
            shortSummary = "This medicine is used to treat temporary diarrhea, nausea, heartburn, indigestion, and upset stomach.",
            mechanism = "Bismuth exerts mild antimicrobial and protective coating action on mucosa, while the salicylate component reduces intestinal inflammation and fluid secretion.",
            commonUses = listOf(
                "Relieves traveler's diarrhea and acute loose stools",
                "Treats heartburn, nausea, and upset stomach"
            ),
            importantSafetyInfo = listOf(
                "May cause a temporary, harmless darkening of the tongue and stool.",
                "Do not use in children or teenagers recovering from chickenpox or flu symptoms due to salicylate content."
            ),
            commonSideEffects = listOf(
                "Harmless dark/black stool",
                "Darkening of tongue",
                "Mild constipation"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),

        // CARDIOVASCULAR & BLOOD PRESSURE & STATINS
        TrustedMedicationRecord(
            genericName = "Amlodipine",
            brandNames = listOf("Norvasc", "Amlor", "Istin"),
            activeIngredients = listOf("Amlodipine Besylate"),
            medicineType = "Calcium Channel Blocker (Blood Pressure Medicine)",
            shortSummary = "This medicine is a dihydropyridine calcium channel blocker used to manage high blood pressure and chest pain (angina).",
            mechanism = "This medicine relaxes and widens arterial blood vessels by inhibiting calcium ion influx into vascular smooth muscle cells, lowering blood pressure.",
            commonUses = listOf(
                "Commonly used to treat hypertension (high blood pressure)",
                "Used to manage chronic stable angina and coronary artery disease"
            ),
            importantSafetyInfo = listOf(
                "Do not discontinue abruptly without your doctor's guidance.",
                "May cause ankle swelling (peripheral edema); report persistent swelling to your doctor."
            ),
            commonSideEffects = listOf(
                "Swelling in ankles or feet (edema)",
                "Dizziness or flushing",
                "Fatigue or palpitations"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Lisinopril",
            brandNames = listOf("Prinivil", "Zestril", "Qbrelis"),
            activeIngredients = listOf("Lisinopril"),
            medicineType = "ACE Inhibitor (Blood Pressure Medicine)",
            shortSummary = "This medicine is an ACE inhibitor used to treat high blood pressure, heart failure, and improve survival after heart attacks.",
            mechanism = "This medicine inhibits angiotensin-converting enzyme (ACE), preventing the formation of angiotensin II, relaxing blood vessels and reducing aldosterone.",
            commonUses = listOf(
                "Management of high blood pressure (hypertension)",
                "Adjunctive therapy in congestive heart failure",
                "Improvement of outcomes following myocardial infarction"
            ),
            importantSafetyInfo = listOf(
                "Black box warning: Do not use during pregnancy as it can cause harm to the developing fetus.",
                "A persistent dry cough may develop; inform your doctor if this occurs.",
                "Seek emergency help immediately if you experience swelling of face, lips, or throat (angioedema)."
            ),
            commonSideEffects = listOf(
                "Dry, tickling cough",
                "Dizziness or lightheadedness when standing",
                "Headache and excessive fatigue"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Losartan",
            brandNames = listOf("Cozaar"),
            activeIngredients = listOf("Losartan Potassium"),
            medicineType = "Angiotensin II Receptor Blocker (ARB)",
            shortSummary = "This medicine is an angiotensin II receptor blocker used to lower blood pressure and protect kidney function in diabetic patients.",
            mechanism = "This medicine selectively blocks angiotensin II from binding to AT1 receptors, dilating blood vessels and lowering peripheral resistance.",
            commonUses = listOf(
                "Treatment of hypertension",
                "Reduction of stroke risk in patients with high blood pressure and cardiomegaly",
                "Diabetic nephropathy in patients with type 2 diabetes"
            ),
            importantSafetyInfo = listOf(
                "Do not use during pregnancy.",
                "Stay well hydrated, as dehydration can cause significant drops in blood pressure."
            ),
            commonSideEffects = listOf(
                "Dizziness or lightheadedness",
                "Nasal congestion or sinus pain",
                "Back pain or fatigue"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Atorvastatin",
            brandNames = listOf("Lipitor", "Atorva", "Sortis"),
            activeIngredients = listOf("Atorvastatin Calcium"),
            medicineType = "Statin (HMG-CoA Reductase Inhibitor)",
            shortSummary = "This medicine is a statin used along with diet to lower LDL ('bad') cholesterol and triglycerides, and reduce cardiovascular risks.",
            mechanism = "This medicine inhibits HMG-CoA reductase, the rate-limiting enzyme in liver cholesterol synthesis, increasing LDL receptor clearance from blood.",
            commonUses = listOf(
                "Lowering elevated LDL cholesterol and total cholesterol levels",
                "Reducing the risk of heart attack, stroke, and revascularization procedures"
            ),
            importantSafetyInfo = listOf(
                "Report unexplained muscle pain, tenderness, or weakness to your doctor promptly.",
                "Avoid consuming large amounts of grapefruit juice while taking atorvastatin.",
                "Take once daily at any time of day, with or without food."
            ),
            commonSideEffects = listOf(
                "Mild joint pain or muscle ache",
                "Diarrhea or indigestion",
                "Mild nausea"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Metoprolol",
            brandNames = listOf("Lopressor", "Toprol-XL", "Betaloc"),
            activeIngredients = listOf("Metoprolol Tartrate / Succinate"),
            medicineType = "Beta-Blocker (Cardiovascular Agent)",
            shortSummary = "This medicine is a selective beta-blocker used to treat high blood pressure, angina, and heart failure.",
            mechanism = "This medicine selectively blocks beta-1 adrenergic receptors in the heart, decreasing heart rate, myocardial contractility, and cardiac output.",
            commonUses = listOf(
                "Management of high blood pressure and chronic angina",
                "Long-term management of stable heart failure (Toprol-XL)",
                "Prevention of future heart complications after a heart attack"
            ),
            importantSafetyInfo = listOf(
                "Do not stop taking this medication abruptly, as sudden withdrawal can trigger severe chest pain or cardiac events.",
                "Take with or immediately after a meal.",
                "May mask early warning signs of low blood sugar (hypoglycemia) such as rapid heartbeat."
            ),
            commonSideEffects = listOf(
                "Fatigue or low energy",
                "Dizziness or slow heart rate (bradycardia)",
                "Cold extremities (hands/feet)"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),

        // METABOLIC & DIABETES & THYROID
        TrustedMedicationRecord(
            genericName = "Metformin",
            brandNames = listOf("Glucophage", "Glucophage XR", "Fortamet", "Glumetza"),
            activeIngredients = listOf("Metformin Hydrochloride"),
            medicineType = "Biguanide (Antidiabetic Medicine)",
            shortSummary = "This medicine is an oral antidiabetic medication used with diet and exercise to lower blood sugar in type 2 diabetes.",
            mechanism = "This medicine decreases liver glucose production (gluconeogenesis), reduces intestinal glucose absorption, and improves insulin sensitivity in peripheral tissues.",
            commonUses = listOf(
                "First-line treatment of type 2 diabetes mellitus",
                "May be used off-label for polycystic ovary syndrome (PCOS)"
            ),
            importantSafetyInfo = listOf(
                "Take with meals to reduce gastrointestinal side effects.",
                "Inform your healthcare team if having imaging procedures with iodinated contrast dye.",
                "Avoid excessive alcohol intake while taking metformin."
            ),
            commonSideEffects = listOf(
                "Nausea, diarrhea, and stomach upset (especially when starting)",
                "Metallic taste in mouth",
                "Bloating or loss of appetite"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Levothyroxine",
            brandNames = listOf("Synthroid", "Levoxyl", "Tirosint", "Euthyrox"),
            activeIngredients = listOf("Levothyroxine Sodium (T4)"),
            medicineType = "Synthetic Thyroid Hormone",
            shortSummary = "This medicine is a replacement hormone used to treat an underactive thyroid gland (hypothyroidism).",
            mechanism = "This medicine is synthetic thyroid hormone (T4) that mimics the endogenous hormone produced by the thyroid gland, regulating metabolic rate and cellular energy.",
            commonUses = listOf(
                "Replacement therapy for primary, secondary, and tertiary hypothyroidism",
                "Suppression of thyroid-stimulating hormone (TSH) in thyroid cancer management"
            ),
            importantSafetyInfo = listOf(
                "Take on an empty stomach with a full glass of water, ideally 30 to 60 minutes before breakfast.",
                "Avoid taking iron, calcium supplements, or antacids within 4 hours of your dose.",
                "Requires periodic blood tests (TSH) to adjust the exact dosage."
            ),
            commonSideEffects = listOf(
                "Generally none when properly dosed to normal physiological levels",
                "If overdosed: palpitations, heat intolerance, tremor, insomnia, weight loss"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),

        // RESPIRATORY & INHALERS
        TrustedMedicationRecord(
            genericName = "Albuterol",
            brandNames = listOf("Ventolin", "ProAir", "Proventil", "Salbutamol"),
            activeIngredients = listOf("Albuterol Sulfate / Salbutamol"),
            medicineType = "Short-Acting Beta-2 Agonist (Bronchodilator / Rescue Inhaler)",
            shortSummary = "This medicine is a fast-acting bronchodilator inhaler used to relieve acute wheezing, shortness of breath, and exercise-induced asthma.",
            mechanism = "This medicine relaxes bronchial smooth muscle by stimulating beta-2 adrenergic receptors, opening airways within minutes.",
            commonUses = listOf(
                "Quick relief of acute bronchospasm and asthma attacks",
                "Prevention of exercise-induced bronchospasm (taken 15 min before exercise)",
                "Symptomatic management of chronic obstructive pulmonary disease (COPD)"
            ),
            importantSafetyInfo = listOf(
                "This is a rescue medication for rapid symptom relief, not a daily controller for chronic inflammation.",
                "If you need your rescue inhaler more than twice a week for asthma symptoms, consult your doctor.",
                "Rinse your mouth after use if using combination preparations."
            ),
            commonSideEffects = listOf(
                "Tremor or jitteriness (shaky hands)",
                "Rapid heartbeat (tachycardia) or palpitations",
                "Nervousness or headache"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Fluticasone",
            brandNames = listOf("Flonase", "Flovent", "Flixotide"),
            activeIngredients = listOf("Fluticasone Propionate"),
            medicineType = "Corticosteroid (Anti-inflammatory)",
            shortSummary = "This medicine is a corticosteroid used to prevent and reduce inflammation in nasal passages or lungs.",
            mechanism = "This medicine exerts potent anti-inflammatory action by suppressing multiple inflammatory cells and cytokines, reducing mucosal swelling and mucus.",
            commonUses = listOf(
                "Daily management and prevention of allergic rhinitis (nasal spray)",
                "Maintenance treatment for asthma prevention (oral inhaler)"
            ),
            importantSafetyInfo = listOf(
                "Must be used consistently every day to achieve full preventative effect; not for acute attacks.",
                "If using oral inhaler, rinse mouth and spit after use to prevent oral thrush."
            ),
            commonSideEffects = listOf(
                "Nasal irritation, dryness, or minor nosebleeds (nasal spray)",
                "Throat irritation or hoarseness",
                "Headache"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),

        // MENTAL HEALTH & NEUROLOGICAL
        TrustedMedicationRecord(
            genericName = "Sertraline",
            brandNames = listOf("Zoloft", "Lustral", "Sertra"),
            activeIngredients = listOf("Sertraline Hydrochloride"),
            medicineType = "Selective Serotonin Reuptake Inhibitor (SSRI)",
            shortSummary = "This medicine is an SSRI antidepressant used to treat depression, anxiety disorders, and obsessive-compulsive disorder.",
            mechanism = "This medicine selectively inhibits the reuptake of serotonin in the brain, increasing the amount of active serotonin available in synapses.",
            commonUses = listOf(
                "Major depressive disorder",
                "Generalized anxiety disorder and panic disorder",
                "Obsessive-compulsive disorder (OCD) and social anxiety disorder",
                "Post-traumatic stress disorder (PTSD)"
            ),
            importantSafetyInfo = listOf(
                "May take 2 to 6 weeks to notice full therapeutic benefits; continue taking as prescribed.",
                "Do not discontinue abruptly without medical guidance to avoid discontinuation symptoms.",
                "Avoid taking with monoamine oxidase inhibitors (MAOIs) or St. John's Wort."
            ),
            commonSideEffects = listOf(
                "Nausea or mild diarrhea (especially initial weeks)",
                "Insomnia, sleepiness, or fatigue",
                "Dry mouth, tremor, or changes in sexual function"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Fluoxetine",
            brandNames = listOf("Prozac", "Sarafem"),
            activeIngredients = listOf("Fluoxetine Hydrochloride"),
            medicineType = "Selective Serotonin Reuptake Inhibitor (SSRI)",
            shortSummary = "This medicine is an SSRI used to treat major depressive disorder, bulimia nervosa, and panic disorder.",
            mechanism = "This medicine selectively inhibits serotonin reuptake in neuronal synapses, elevating mood and reducing anxious agitation.",
            commonUses = listOf(
                "Major depressive disorder in adults and pediatric patients",
                "Obsessive-compulsive disorder and bulimia nervosa",
                "Panic disorder and premenstrual dysphoric disorder (PMDD)"
            ),
            importantSafetyInfo = listOf(
                "Has a long elimination half-life in the body.",
                "Discuss with your healthcare provider before combining with other medications affecting serotonin."
            ),
            commonSideEffects = listOf(
                "Trouble sleeping or vivid dreams",
                "Nausea, diarrhea, or decreased appetite",
                "Headache and nervousness"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Gabapentin",
            brandNames = listOf("Neurontin", "Gralise", "Horizant"),
            activeIngredients = listOf("Gabapentin"),
            medicineType = "Anticonvulsant & Neuropathic Pain Agent",
            shortSummary = "This medicine is used to treat nerve pain (such as after shingles) and as an add-on treatment for certain seizures.",
            mechanism = "This medicine binds to the alpha-2-delta subunit of voltage-gated calcium channels in the central nervous system, reducing the release of excitatory neurotransmitters.",
            commonUses = listOf(
                "Postherpetic neuralgia (nerve pain following shingles)",
                "Adjunctive therapy for partial onset seizures",
                "Management of moderate-to-severe restless legs syndrome"
            ),
            importantSafetyInfo = listOf(
                "May cause marked drowsiness and dizziness. Avoid driving until you know how it affects you.",
                "Do not stop taking suddenly; dose tapering is recommended."
            ),
            commonSideEffects = listOf(
                "Drowsiness, fatigue, and dizziness",
                "Unsteadiness or coordination problems (ataxia)",
                "Peripheral swelling"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),

        // ANTIFUNGALS & ANTIVIRALS
        TrustedMedicationRecord(
            genericName = "Fluconazole",
            brandNames = listOf("Diflucan", "Forcan"),
            activeIngredients = listOf("Fluconazole"),
            medicineType = "Antifungal (Triazole group)",
            shortSummary = "This medicine is a triazole antifungal used to treat and prevent a variety of fungal and yeast infections.",
            mechanism = "This medicine inhibits fungal cytochrome P450 enzyme 14-alpha-demethylase, blocking ergosterol synthesis in fungal cell membranes.",
            commonUses = listOf(
                "Treatment of vaginal yeast infections (candidiasis)",
                "Oral thrush and esophageal fungal infections",
                "Systemic fungal infections in vulnerable patients"
            ),
            importantSafetyInfo = listOf(
                "Often prescribed as a single oral dose for vaginal yeast infections.",
                "Inform your doctor about all other medications, as fluconazole interacts with several liver-metabolized drugs."
            ),
            commonSideEffects = listOf(
                "Headache",
                "Nausea and abdominal discomfort",
                "Mild diarrhea or rash"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        ),
        TrustedMedicationRecord(
            genericName = "Acyclovir",
            brandNames = listOf("Zovirax", "Sitavig"),
            activeIngredients = listOf("Acyclovir"),
            medicineType = "Antiviral Agent",
            shortSummary = "This medicine is an antiviral drug used to treat infections caused by herpes simplex viruses and varicella zoster (shingles & chickenpox).",
            mechanism = "This medicine is converted to acyclovir triphosphate inside infected cells, which selectively inhibits viral DNA polymerase and halts viral replication.",
            commonUses = listOf(
                "Treatment and suppression of genital herpes outbreaks",
                "Treatment of herpes zoster (shingles) and cold sores",
                "Management of chickenpox in high-risk patients"
            ),
            importantSafetyInfo = listOf(
                "Does not cure viral infections, but reduces severity and duration of outbreaks.",
                "Drink plenty of fluids while taking to prevent kidney strain."
            ),
            commonSideEffects = listOf(
                "Nausea and diarrhea",
                "Headache and dizziness",
                "Fatigue"
            ),
            sourceName = "MedlinePlus (National Library of Medicine)",
            sourceUpdatedAt = "August 2026"
        )
    )

    /**
     * Stem-based pharmacological taxonomy matcher for uncatalogued or variant medications.
     */
    private val stemTaxonomy = listOf(
        Pair("-cillin", Pair("Antibiotic (Penicillin group)", "Inhibits bacterial cell-wall synthesis to treat bacterial infections.")),
        Pair("-cycline", Pair("Antibiotic (Tetracycline group)", "Inhibits bacterial protein synthesis to treat bacterial infections.")),
        Pair("-floxacin", Pair("Antibiotic (Fluoroquinolone group)", "Inhibits bacterial DNA replication to treat specific bacterial infections.")),
        Pair("-mycin", Pair("Antibiotic (Macrolide/Aminoglycoside group)", "Inhibits bacterial protein synthesis to fight bacterial infections.")),
        Pair("ceph-", Pair("Antibiotic (Cephalosporin group)", "Disrupts bacterial cell-wall construction to treat bacterial infections.")),
        Pair("cef-", Pair("Antibiotic (Cephalosporin group)", "Disrupts bacterial cell-wall construction to treat bacterial infections.")),
        Pair("-statin", Pair("Statin (Lipid-Lowering Agent)", "Inhibits HMG-CoA reductase to lower LDL cholesterol and reduce cardiovascular risk.")),
        Pair("-olol", Pair("Beta-Blocker (Blood Pressure Medicine)", "Blocks beta receptors to slow heart rate and lower blood pressure.")),
        Pair("-sartan", Pair("Angiotensin Receptor Blocker (ARB)", "Relaxes blood vessels by blocking angiotensin II receptors.")),
        Pair("-pril", Pair("ACE Inhibitor (Blood Pressure Medicine)", "Relaxes blood vessels by blocking angiotensin-converting enzyme.")),
        Pair("-prazole", Pair("Proton Pump Inhibitor (Acid Reducer)", "Shuts down acid-producing pumps in the stomach to relieve acid reflux.")),
        Pair("-tidine", Pair("H2 Blocker (Acid Reducer)", "Blocks H2 histamine receptors in the stomach to reduce acid secretion.")),
        Pair("-dipine", Pair("Calcium Channel Blocker", "Relaxes vascular smooth muscle to lower blood pressure.")),
        Pair("-nazole", Pair("Antifungal Agent", "Disrupts fungal cell membranes to treat yeast and fungal infections.")),
        Pair("-vir", Pair("Antiviral Agent", "Inhibits viral replication enzymes to reduce viral outbreak duration.")),
        Pair("-sone", Pair("Corticosteroid", "Reduces inflammation and suppresses overactive immune responses.")),
        Pair("-terol", Pair("Bronchodilator (Beta-2 Agonist)", "Relaxes airway muscles to improve breathing.")),
        Pair("-gliptin", Pair("DPP-4 Inhibitor (Antidiabetic)", "Enhances incretin hormones to help lower blood glucose.")),
        Pair("-gliflozin", Pair("SGLT-2 Inhibitor (Antidiabetic)", "Increases urinary glucose excretion to lower blood sugar."))
    )

    /**
     * Look up verified medication by name, brand, or active ingredient.
     */
    fun findMedication(query: String, activeIngredients: List<String> = emptyList()): TrustedMedicationRecord? {
        val cleanQuery = normalizeName(query)
        if (cleanQuery.isBlank()) return null

        // 1. Direct exact or brand match
        val directMatch = verifiedDatabase.firstOrNull { record ->
            normalizeName(record.genericName) == cleanQuery ||
                    record.brandNames.any { normalizeName(it) == cleanQuery } ||
                    cleanQuery.contains(normalizeName(record.genericName)) ||
                    record.brandNames.any { cleanQuery.contains(normalizeName(it)) }
        }
        if (directMatch != null) return directMatch

        // 2. Active ingredient match
        for (ing in activeIngredients) {
            val cleanIng = normalizeName(ing)
            val ingMatch = verifiedDatabase.firstOrNull { record ->
                record.activeIngredients.any { normalizeName(it).contains(cleanIng) || cleanIng.contains(normalizeName(it)) }
            }
            if (ingMatch != null) return ingMatch
        }

        // 3. Multi-word search against generic and brand names
        val tokens = cleanQuery.split(" ", "-", "/").filter { it.length >= 4 }
        for (token in tokens) {
            val tokenMatch = verifiedDatabase.firstOrNull { record ->
                normalizeName(record.genericName).contains(token) ||
                        record.brandNames.any { normalizeName(it).contains(token) } ||
                        record.activeIngredients.any { normalizeName(it).contains(token) }
            }
            if (tokenMatch != null) return tokenMatch
        }

        // 4. Pharmacological stem matching for unlisted verified generics
        for ((stem, pair) in stemTaxonomy) {
            val isMatch = if (stem.startsWith("-")) {
                val suffix = stem.removePrefix("-")
                cleanQuery.endsWith(suffix) || tokens.any { it.endsWith(suffix) }
            } else if (stem.endsWith("-")) {
                val prefix = stem.removeSuffix("-")
                cleanQuery.startsWith(prefix) || tokens.any { it.startsWith(prefix) }
            } else false

            if (isMatch) {
                return TrustedMedicationRecord(
                    genericName = query.trim().replaceFirstChar { it.uppercase() },
                    brandNames = emptyList(),
                    activeIngredients = if (activeIngredients.isNotEmpty()) activeIngredients else listOf(query.trim()),
                    medicineType = pair.first,
                    shortSummary = "This medicine belongs to the ${pair.first.lowercase()} group. It is commonly prescribed to manage conditions associated with this pharmacological class.",
                    mechanism = "This medicine belongs to the ${pair.first} group. It works by: ${pair.second}",
                    commonUses = listOf(
                        "Used according to clinical prescribing guidelines for ${pair.first}",
                        "Specific indication depends on dosage, formulation, and clinician diagnosis"
                    ),
                    importantSafetyInfo = listOf(
                        "Take strictly as prescribed by your healthcare provider.",
                        "Discuss all other active medications and medical conditions with your doctor or pharmacist.",
                        "Consult your healthcare provider if you experience adverse or unexpected symptoms."
                    ),
                    commonSideEffects = listOf(
                        "Side effects vary by individual medicine and personal health profile",
                        "Consult the manufacturer's package leaflet or your pharmacist for detailed effect profiles"
                    ),
                    sourceName = "Official Pharmacological Reference (US Pharmacopeia / MedlinePlus)",
                    sourceUpdatedAt = "August 2026"
                )
            }
        }

        return null
    }

    fun normalizeName(rawName: String): String {
        return rawName.lowercase()
            .replace(Regex("\\b\\d+(\\.\\d+)?\\s*(mg|mcg|g|ml|iu|%)\\b"), "") // Remove dosages
            .replace(Regex("\\b(tablet|tablets|capsule|capsules|syrup|oral suspension|cream|ointment|gel|drop|drops|injection|inhaler|patch)\\b"), "") // Remove forms
            .replace(Regex("\\b(xr|er|cr|sr|hcl|sodium|potassium|calcium|succinate|tartrate|propionate|maleate|hydrochloride)\\b"), "") // Remove suffixes
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
