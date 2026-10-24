package com.example.data

object DefaultMockDataExams {
    val mocks: List<TypingMockEntity> = listOf(
        // RRB NTPC - 10 Minutes, 1500+ words passage, 30 Net WPM required, 5% mistakes limit
        TypingMockEntity(
            id = "ntpc_1",
            title = "RRB NTPC: Indian Railways Modernization & Vande Bharat",
            level = 2,
            examTag = "ntpc",
            durationMinutes = 10,
            targetKeystrokes = 1500,
            wordCount = 420,
            cutoffGeneral = 5.0,
            cutoffEws = 5.0,
            cutoffObc = 5.0,
            cutoffSc = 5.0,
            cutoffSt = 5.0,
            cutoffPwd = 5.0,
            minSpeedWpm = 30,
            text = "The Indian Railways network, encompassing over 68,000 route kilometers and carrying more than twenty-two million passengers daily, is undergoing its most comprehensive technological modernization in post-independence history. At the forefront of this passenger experience revolution is the semi-high-speed Vande Bharat Express, engineered and manufactured indigenously by the Integral Coach Factory (ICF) in Chennai under the Make in India initiative. Operating at service speeds of up to 160 kilometers per hour, these trainsets eliminate traditional locomotive haulage by employing distributed traction power where electric motors are mounted directly onto bogies across alternate coaches. This design delivers superior acceleration and deceleration, cutting inter-city transit times by fifteen to twenty-five percent compared to conventional Rajdhani and Shatabdi rakes. The aerodynamic coach bodies are constructed from lightweight stainless steel and feature fully automated plug doors, touch-free bio-vacuum toilets, GPS-based passenger information systems, and regenerative braking that feeds up to thirty percent of kinetic electrical energy back into overhead traction lines. Simultaneously, the Indian Railways has achieved nearly one hundred percent broad-gauge track electrification, phasing out diesel traction and curtailing the national fuel import bill by over 15,000 crore rupees annually. With the introduction of sleeper-class Vande Bharat variants and modern automated train testing facilities, rail connectivity is rapidly transforming into a world-class, energy-efficient transport corridor."
        ),
        TypingMockEntity(
            id = "ntpc_2",
            title = "RRB NTPC: Electronic Interlocking & Track Safety",
            level = 2,
            examTag = "ntpc",
            durationMinutes = 10,
            targetKeystrokes = 1500,
            wordCount = 415,
            cutoffGeneral = 5.0,
            cutoffEws = 5.0,
            cutoffObc = 5.0,
            cutoffSc = 5.0,
            cutoffSt = 5.0,
            cutoffPwd = 5.0,
            minSpeedWpm = 30,
            text = "Operational safety on dense railway networks relies essentially on advanced track infrastructure and fail-safe electronic signalling systems. Traditional mechanical lever frames, which depended on manual pulling of wire lines to set track turnouts and lower semaphore signals, have been systematically replaced by microprocessor-based Electronic Interlocking (EI) installations at over six thousand stations. Electronic interlocking utilizes dual-redundant logic controllers that constantly verify track circuit clearance, route setting, and signal aspects before releasing a green signal to an approaching train, making it virtually impossible to display conflicting indications to two trains simultaneously. In addition, Indian Railways has widely introduced Digital Axle Counters (DAC) that count the precise number of wheel axles entering and leaving a designated track section, unaffected by track moisture or rust that occasionally plagued old DC track circuits. Track maintenance has also evolved from manual gangman inspections to high-speed Track Recording Cars (TRC) and ultrasonic rail flaw detectors (USFD) that identify microscopic internal metal fatigue cracks inside rail heads long before fractures can trigger derailments. Long Welded Rails (LWR) spanning several kilometers have eliminated traditional fish-plated rail joints, delivering whisper-quiet ride comfort for passengers while dramatically reducing mechanical wear and tear on wheel sets."
        ),
        TypingMockEntity(
            id = "ntpc_3",
            title = "RRB NTPC: Station Redevelopment & Amrit Bharat Scheme",
            level = 2,
            examTag = "ntpc",
            durationMinutes = 10,
            targetKeystrokes = 1500,
            wordCount = 410,
            cutoffGeneral = 5.0,
            cutoffEws = 5.0,
            cutoffObc = 5.0,
            cutoffSc = 5.0,
            cutoffSt = 5.0,
            cutoffPwd = 5.0,
            minSpeedWpm = 30,
            text = "Railway stations across India are shedding their colonial identities to re-emerge as modern multimodal transport hubs under the Amrit Bharat Station Scheme. Covering more than 1,300 stations nationwide, the redevelopment blueprint focuses on master planning for the next fifty years, integrating city centers separated by railway tracks through wide roof plazas and elevated concourses. Under this architecture, passenger arrivals and departures are segregated to prevent platform congestion, mirroring international airport standards. The roof plazas house waiting lounges, food courts, children play zones, and dedicated retail spaces showcasing local indigenous handicrafts under the 'One Station One Product' initiative. Universal accessibility features for elderly and differently-abled passengers (Divyangjan) include tactile flooring, braille directional signages, automatic escalators, wide elevators, and step-free battery-operated platform cars. Green building certifications are mandatory, requiring stations to incorporate rooftop solar panels, rainwater harvesting tanks, LED fixtures, and on-site solid waste recycling plants. Stations such as Rani Kamlapati in Bhopal and Sir M. Visvesvaraya Terminal in Bengaluru stand as stellar examples of world-class rail hospitality that boost regional tourism and stimulate local commercial activity."
        ),

        // SSC CGL DEST - 15 Minutes, 2000 Key Depressions (8000 KDPH / 27 WPM)
        TypingMockEntity(
            id = "cgl_1",
            title = "SSC CGL DEST: Union Budget & Fiscal Management",
            level = 3,
            examTag = "cgl",
            durationMinutes = 15,
            targetKeystrokes = 2000,
            wordCount = 390,
            cutoffGeneral = 5.0,
            cutoffEws = 7.0,
            cutoffObc = 7.0,
            cutoffSc = 7.0,
            cutoffSt = 7.0,
            cutoffPwd = 10.0,
            minSpeedWpm = 27,
            text = "The Union Budget of India, officially designated as the Annual Financial Statement under Article 112 of the Constitution, embodies the central government's estimated revenue receipts and capital expenditure for each financial year. Presented in Parliament on the first day of February, the budgetary cycle involves comprehensive legislative scrutiny through general discussions, departmental standing committee reviews, voting on demands for grants, and the ultimate enactment of the Appropriation Bill and Finance Bill. In recent years, fiscal consolidation strategies under the Fiscal Responsibility and Budget Management (FRBM) Act have sought to progressively curtail the fiscal deficit toward 4.5 percent of gross domestic product while significantly enhancing capital expenditure (Capex) on physical infrastructure including national highways, logistics parks, and digital connectivity. Capital investments generate a powerful multiplier effect of more than three times on the economy compared to revenue consumption spending. Simultaneously, direct tax administration has embraced faceless assessment, automated e-verification, and simplified tax slabs, drastically reducing tax litigation and fostering voluntary compliance among millions of individual taxpayers and corporate enterprises."
        ),
        TypingMockEntity(
            id = "cgl_2",
            title = "SSC CGL DEST: Tax Administration & Faceless Assessment",
            level = 3,
            examTag = "cgl",
            durationMinutes = 15,
            targetKeystrokes = 2000,
            wordCount = 385,
            cutoffGeneral = 5.0,
            cutoffEws = 7.0,
            cutoffObc = 7.0,
            cutoffSc = 7.0,
            cutoffSt = 7.0,
            cutoffPwd = 10.0,
            minSpeedWpm = 27,
            text = "To eliminate human discretion, eradicate systemic corruption, and establish a taxpayer-friendly environment, the Central Board of Direct Taxes (CBDT) instituted the National Faceless Assessment Centre (NFAC). Under this technological paradigm, tax scrutiny notices, submissions, and penalty determinations are processed entirely through digital communication channels without requiring any physical interaction between the assessee and the tax assessing officer. Cases are allocated across dynamic assessment units nationwide using automated artificial intelligence algorithms, ensuring that a taxpayer residing in Chennai may be assessed by an officer located in Jaipur or Kolkata with complete territorial anonymity. Every communication bears a computer-generated Document Identification Number (DIN) to prevent unauthorized inquiries. Concurrently, the Taxpayers' Charter guarantees prompt service delivery, transparent dispute resolution, and confidentiality of financial disclosures. By integrating financial transaction reports with the Annual Information Statement (AIS) available on the e-filing portal, the income tax administration has made tax compliance seamless, transparent, and legally accountable for all law-abiding citizens."
        ),

        // SSC CHSL LDC & DEO - 10 / 15 Minutes, 35 WPM / 8000 KDPH
        TypingMockEntity(
            id = "chsl_1",
            title = "SSC CHSL: Right to Information & Transparent Governance",
            level = 2,
            examTag = "chsl_ldc",
            durationMinutes = 10,
            targetKeystrokes = 1750,
            wordCount = 370,
            cutoffGeneral = 7.0,
            cutoffEws = 10.0,
            cutoffObc = 10.0,
            cutoffSc = 10.0,
            cutoffSt = 10.0,
            cutoffPwd = 10.0,
            minSpeedWpm = 35,
            text = "Enacted in 2005, the Right to Information (RTI) Act stands as a watershed milestone in Indian administrative jurisprudence, empowering ordinary citizens to hold public authorities accountable for their official decisions, financial expenditures, and public delivery commitments. Under Section 4 of the statute, all public authorities are mandated to make suo motu proactive disclosures concerning their organizational structures, employee powers, budget allocations, and beneficiary schemes through accessible digital web portals. Any citizen may submit an information request to the designated Public Information Officer (PIO), who is legally obligated to provide certified copies within thirty calendar days, or within forty-eight hours if the requested information touches upon life and liberty. The Act establishes an independent appellate framework comprising First Appellate Authorities and Central or State Information Commissions, empowered to impose penal sanctions on defaulting officials. By pulling back the bureaucratic veil of official secrecy, RTI has unmasked systemic inefficiencies, facilitated social audits in rural employment guarantee programs, and nurtured a culture of participatory democracy where citizens transition from passive recipients of government services to vigilant custodians of the public interest."
        ),
        TypingMockEntity(
            id = "chsl_2",
            title = "SSC CHSL DEO: Citizen Charters & Public Grievance Redressal",
            level = 2,
            examTag = "chsl_deo",
            durationMinutes = 15,
            targetKeystrokes = 2000,
            wordCount = 380,
            cutoffGeneral = 5.0,
            cutoffEws = 7.0,
            cutoffObc = 7.0,
            cutoffSc = 7.0,
            cutoffSt = 7.0,
            cutoffPwd = 10.0,
            minSpeedWpm = 27,
            text = "Good governance requires that public administration be transparent, responsive, and prompt in addressing citizen grievances. The Citizen's Charter is a public declaration of the vision, mission, and standard service delivery timelines of government departments, specifying clear benchmarks against which operational performance can be measured. To ensure that grievances do not languish in bureaucratic corridors, the Department of Administrative Reforms and Public Grievances (DARPG) operates the Centralized Public Grievance Redress and Monitoring System (CPGRAMS). Accessible via a single mobile application and web portal, CPGRAMS enables citizens to lodge complaints against any central ministry or state department round the clock. Complaints are tracked with unique registration numbers and assigned strict time limits for redressal, typically not exceeding thirty days. Senior administrative secretaries review grievance patterns periodically to identify chronic systemic bottlenecks in service delivery. Furthermore, independent quality audits and telephonic feedback mechanisms assess citizen satisfaction following grievance resolution. Transforming bureaucratic culture into an empathetic, service-oriented delivery mechanism is essential for deepening public trust in democratic institutions."
        ),

        // DSSSB (JA/LDC) - 10 Minutes, 35 WPM, strict 3% - 5% error limit
        TypingMockEntity(
            id = "dsssb_1",
            title = "DSSSB JA: Urban Governance & Metropolitan Planning",
            level = 3,
            examTag = "dsssb",
            durationMinutes = 10,
            targetKeystrokes = 1750,
            wordCount = 370,
            cutoffGeneral = 3.0,
            cutoffEws = 5.0,
            cutoffObc = 5.0,
            cutoffSc = 5.0,
            cutoffSt = 5.0,
            cutoffPwd = 7.0,
            minSpeedWpm = 35,
            text = "Rapid urbanization in Indian metropolitan territories presents profound governance challenges, demanding integrated urban planning, spatial data modeling, and decentralized municipal administration under the 74th Constitutional Amendment Act. Mega-cities like the National Capital Territory of Delhi require delicate coordination between municipal corporations, urban development authorities, water boards, and power utilities to manage burgeoning populations exceeding twenty million residents. Master Plans for Delhi focus on transit-oriented development (TOD), where high-density mixed land-use zones are strategically clustered around Mass Rapid Transit System (MRTS) metro stations, minimizing private vehicle dependence and containing urban sprawl. Concurrently, municipal authorities are deploying automated SCADA systems for municipal water distribution, smart water meters to curb non-revenue water loss, and decentralized waste-to-energy power plants to process municipal solid waste. Protecting natural drainage channels, expanding urban green buffers, and revitalizing historic stepwells and lakes ensure ecological sustainability while accommodating resilient economic growth."
        ),

        // KVS / NVS (JSA) - 10 Minutes, 35 WPM / 30 WPM, CBSE evaluation
        TypingMockEntity(
            id = "kvs_1",
            title = "KVS / NVS JSA: National Education Policy (NEP 2020)",
            level = 2,
            examTag = "kvs_nvs",
            durationMinutes = 10,
            targetKeystrokes = 1750,
            wordCount = 375,
            cutoffGeneral = 7.0,
            cutoffEws = 10.0,
            cutoffObc = 10.0,
            cutoffSc = 10.0,
            cutoffSt = 10.0,
            cutoffPwd = 10.0,
            minSpeedWpm = 35,
            text = "The National Education Policy (NEP 2020) articulates a transformative roadmap for revamping India's school and higher education ecosystem into an equitable, vibrant knowledge society. Overhauling the traditional 10+2 curricular structure, the policy introduces an age-appropriate 5+3+3+4 pedagogical framework covering foundational (ages 3-8), preparatory (ages 8-11), middle (ages 11-14), and secondary (ages 14-18) stages. The foundational stage places paramount emphasis on universal Foundational Literacy and Numeracy (FLN) by Grade 3 through play-based and activity-oriented learning under the NIPUN Bharat Mission. NEP 2020 promotes multilingualism, encouraging mother tongue or regional language instruction at least until Grade 5 to enhance cognitive comprehension. In addition, the rigid bifurcation between arts and sciences, curricular and co-curricular pursuits, or academic and vocational streams has been systematically dismantled. Vocational training, computational thinking, coding, and environmental ethics are woven into the middle school curriculum, preparing youth with critical thinking skills suited for 21st-century technological landscapes."
        ),

        // Hindi Typing Mocks - Inscript Layout
        TypingMockEntity(
            id = "hindi_1",
            title = "हिंदी टाइपिंग: भारत में डिजिटल क्रांति और जन-धन योजना",
            level = 2,
            examTag = "hindi",
            durationMinutes = 10,
            targetKeystrokes = 1500,
            wordCount = 310,
            cutoffGeneral = 7.0,
            cutoffEws = 10.0,
            cutoffObc = 10.0,
            cutoffSc = 10.0,
            cutoffSt = 10.0,
            cutoffPwd = 10.0,
            minSpeedWpm = 30,
            text = "भारत में डिजिटल क्रांति ने आम नागरिक के जीवन को बहुत सरल और सुगम बना दिया है। प्रधानमंत्री जन-धन योजना के तहत करोड़ों गरीब परिवारों के बैंक खाते खोले गए। इससे सरकारी योजनाओं का लाभ सीधे लाभार्थियों के बैंक खातों में बिना किसी बिचौलिए के पहुंचने लगा है। आज गांव-गांव में छोटे व्यापारी भी मोबाइल फोन से यूपीआई द्वारा डिजिटल भुगतान स्वीकार कर रहे हैं। तकनीक ने न केवल भ्रष्टाचार पर रोक लगाई है बल्कि दूरदराज के क्षेत्रों में रहने वाले नागरिकों को भी मुख्यधारा से जोड़ा है। हमें डिजिटल साक्षरता को और बढ़ावा देना चाहिए ताकि प्रत्येक नागरिक इस आधुनिक व्यवस्था का पूरा लाभ उठा सके और देश प्रगति के नए आयाम स्थापित कर सके।"
        )
    )
}
