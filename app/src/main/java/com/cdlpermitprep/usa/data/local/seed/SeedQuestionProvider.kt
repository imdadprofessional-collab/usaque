package com.cdlpermitprep.usa.data.local.seed

import com.cdlpermitprep.usa.data.local.entity.QuestionEntity

/**
 * Ships a starter question bank so the app works fully offline out of the box.
 * Real content should be imported via a bulk-loading pipeline (CSV/JSON -> Room)
 * capable of scaling to 10k/50k/100k+ rows; this provider is the seed for that pipeline,
 * not the ceiling of the schema.
 */
object SeedQuestionProvider {

    private fun q(
        state: String = "General",
        category: String,
        subCategory: String,
        difficulty: String = "Medium",
        question: String,
        a: String,
        b: String,
        c: String,
        d: String,
        correct: String,
        explanation: String,
        reference: String? = null,
        tags: String = "",
        isPremium: Boolean = false,
    ) = QuestionEntity(
        state = state, category = category, subCategory = subCategory, difficulty = difficulty,
        question = question, optionA = a, optionB = b, optionC = c, optionD = d,
        correctAnswer = correct, explanation = explanation, reference = reference, tags = tags,
        isPremium = isPremium,
    )

    fun all(): List<QuestionEntity> = generalKnowledge() + airBrakes() + combinationVehicles() +
        hazmat() + tankerVehicles() + doublesTriples() + passengerVehicles() + stateSpecific()

    private fun generalKnowledge() = listOf(
        q(
            category = "General Knowledge", subCategory = "Vehicle Inspection", difficulty = "Easy",
            question = "What is the main purpose of a pre-trip inspection?",
            a = "To clean the vehicle", b = "To find problems before they cause a breakdown or crash",
            c = "To fill out paperwork", d = "To check the driver's license",
            correct = "B", explanation = "A pre-trip inspection catches safety defects before you're on the road, where they're far more dangerous to fix.",
            tags = "inspection,safety",
        ),
        q(
            category = "General Knowledge", subCategory = "Braking", difficulty = "Medium",
            question = "Total stopping distance is the sum of which three distances?",
            a = "Perception, reaction, braking", b = "Reaction, following, braking",
            c = "Perception, following, stopping", d = "Braking, turning, reaction",
            correct = "A", explanation = "Stopping distance = perception distance + reaction distance + braking distance.",
            tags = "stopping distance",
        ),
        q(
            category = "General Knowledge", subCategory = "Hydroplaning", difficulty = "Medium",
            question = "At what speed can a truck's tires begin to hydroplane on a wet road?",
            a = "As low as 30 mph", b = "Only above 70 mph", c = "Never, trucks are too heavy",
            d = "Only on ice", correct = "A", explanation = "Hydroplaning can occur at speeds as low as 30 mph if there is enough water on the road.",
            tags = "hydroplaning,wet roads",
        ),
        q(
            category = "General Knowledge", subCategory = "Fatigue", difficulty = "Easy",
            question = "What is the best way to deal with driver fatigue?",
            a = "Drink coffee and keep driving", b = "Open the window for fresh air",
            c = "Stop and get sleep", d = "Turn up the radio",
            correct = "C", explanation = "Only sleep cures fatigue; stimulants and fresh air only mask it temporarily.",
            tags = "fatigue,fitness for duty",
        ),
        q(
            category = "General Knowledge", subCategory = "Skid Control", difficulty = "Hard",
            question = "The most common cause of a skid is:",
            a = "Driving too fast for conditions", b = "Low tire pressure",
            c = "Old windshield wipers", d = "Loose cargo",
            correct = "A", explanation = "Excess speed for the road/weather conditions is the single most common cause of skids.",
            tags = "skid,speed",
        ),
        q(
            category = "General Knowledge", subCategory = "Backing", difficulty = "Medium",
            question = "When backing, you should:",
            a = "Back toward the driver's side whenever possible", b = "Always use a spotter and never turn around to check",
            c = "Back quickly to reduce exposure time", d = "Rely only on mirrors",
            correct = "A", explanation = "Backing toward the driver's side gives you the best direct view of your path.",
            tags = "backing",
        ),
        q(
            category = "General Knowledge", subCategory = "Extreme Driving Conditions", difficulty = "Medium",
            question = "In foggy conditions you should:",
            a = "Use high-beam headlights", b = "Use low-beam headlights and slow down",
            c = "Turn off all lights", d = "Follow the vehicle ahead closely to keep it in sight",
            correct = "B", explanation = "High beams reflect off fog and reduce visibility; low beams plus reduced speed are safer.",
            tags = "fog,visibility",
        ),
        q(
            category = "General Knowledge", subCategory = "Cargo", difficulty = "Medium",
            question = "Why must cargo be balanced and secured?",
            a = "To make loading faster", b = "To prevent shifting that affects handling and to avoid falling cargo",
            c = "It is only a company policy, not a safety issue", d = "To reduce fuel costs",
            correct = "B", explanation = "Unsecured or unbalanced cargo can shift, causing loss of control, or fall and injure others.",
            tags = "cargo,securement",
        ),
        q(
            category = "General Knowledge", subCategory = "Railroad Crossings", difficulty = "Easy",
            question = "How many gears should you avoid shifting through while crossing railroad tracks?",
            a = "You should never shift gears while crossing", b = "It doesn't matter",
            c = "Only shift into a higher gear", d = "Shift as many times as needed",
            correct = "A", explanation = "Never change gears while crossing tracks — select the right gear beforehand so you don't risk stalling.",
            tags = "railroad crossing",
        ),
        q(
            category = "General Knowledge", subCategory = "Vision", difficulty = "Easy",
            question = "How far ahead should you generally look while driving?",
            a = "Just past the hood of the truck", b = "12-15 seconds ahead",
            c = "Only at the vehicle directly in front", d = "There is no need to look ahead",
            correct = "B", explanation = "Looking 12-15 seconds ahead gives you time to react to hazards before they become emergencies.",
            tags = "visual search",
        ),
    )

    private fun airBrakes() = listOf(
        q(
            category = "Air Brakes", subCategory = "System Basics", difficulty = "Medium",
            question = "What warning device activates when air pressure drops below a safe level?",
            a = "The horn", b = "A low air pressure warning light and buzzer",
            c = "The headlights flash", d = "Nothing, brakes just fail silently",
            correct = "B", explanation = "A low air pressure warning signal must activate before pressure drops below 60 psi (or per manufacturer spec).",
            tags = "air brakes,warning",
        ),
        q(
            category = "Air Brakes", subCategory = "Spring Brakes", difficulty = "Medium",
            question = "Spring brakes come on fully when air pressure drops to what range?",
            a = "20-45 psi", b = "60-80 psi", c = "100-120 psi", d = "0 psi only",
            correct = "A", explanation = "Spring brakes activate automatically as pressure falls into the 20-45 psi range, varies by vehicle.",
            tags = "spring brakes",
        ),
        q(
            category = "Air Brakes", subCategory = "Dual System", difficulty = "Hard",
            question = "A dual air brake system has two separate air brake systems, primarily to:",
            a = "Make the truck faster", b = "Provide backup braking if one system fails",
            c = "Reduce the cost of parts", d = "Improve fuel economy",
            correct = "B", explanation = "The dual system means a failure in one circuit still leaves some braking power available.",
            tags = "dual system",
        ),
        q(
            category = "Air Brakes", subCategory = "Inspection", difficulty = "Medium",
            question = "During an air brake pre-trip, how long should you wait to check for excessive air loss with the engine off and brakes released?",
            a = "1 minute, loss should not exceed 2 psi", b = "10 seconds",
            c = "It doesn't need to be checked", d = "5 minutes minimum",
            correct = "A", explanation = "With the engine off, brakes released, air loss should not be more than 2 psi in one minute for a single vehicle.",
            tags = "air loss test",
        ),
    )

    private fun combinationVehicles() = listOf(
        q(
            category = "Combination Vehicles", subCategory = "Coupling", difficulty = "Medium",
            question = "Before coupling a tractor to a semitrailer, you should:",
            a = "Skip inspection to save time", b = "Inspect the fifth wheel and make sure the trailer height is correct",
            c = "Only check the lights", d = "Just back straight in without checking",
            correct = "B", explanation = "Proper trailer height and a clean, undamaged fifth wheel are essential for a safe, secure coupling.",
            tags = "coupling,fifth wheel",
        ),
        q(
            category = "Combination Vehicles", subCategory = "Off-Tracking", difficulty = "Hard",
            question = "What causes 'off-tracking' in combination vehicles?",
            a = "The rear wheels follow a different path than the front wheels in turns", b = "Worn tires only",
            c = "Low air pressure", d = "Excess trailer weight",
            correct = "A", explanation = "Off-tracking (or 'cheating') happens because the rear wheels of a long vehicle take a shorter path than the front wheels while turning.",
            tags = "off-tracking",
        ),
        q(
            category = "Combination Vehicles", subCategory = "Rollover", difficulty = "Hard",
            question = "Combination vehicles are more likely to roll over than straight trucks because:",
            a = "They have a lower center of gravity", b = "They often carry top-heavy loads and have a higher center of gravity",
            c = "They are shorter", d = "They have more axles",
            correct = "B", explanation = "A high center of gravity, especially with top-heavy loads, greatly increases rollover risk in turns.",
            tags = "rollover",
        ),
    )

    private fun hazmat() = listOf(
        q(
            category = "Hazardous Materials", subCategory = "Placarding", difficulty = "Hard", isPremium = true,
            question = "Placards are required on a vehicle carrying hazardous materials primarily to:",
            a = "Decorate the vehicle", b = "Warn others of the danger and guide emergency response",
            c = "Track mileage", d = "Meet company branding rules",
            correct = "B", explanation = "Placards communicate the hazard class to other drivers, inspectors, and emergency responders.",
            tags = "hazmat,placards",
        ),
        q(
            category = "Hazardous Materials", subCategory = "Shipping Papers", difficulty = "Hard", isPremium = true,
            question = "Shipping papers for hazardous materials must be:",
            a = "Locked in the trailer", b = "Easily recognized and within the driver's reach while driving",
            c = "Mailed to the shipper separately", d = "Not required if the load is small",
            correct = "B", explanation = "Shipping papers must be accessible without leaving the driver's seat and easily seen by emergency crews.",
            tags = "shipping papers",
        ),
    )

    private fun tankerVehicles() = listOf(
        q(
            category = "Tank Vehicles", subCategory = "Surge", difficulty = "Hard", isPremium = true,
            question = "What is 'surge' in a tank vehicle?",
            a = "A sudden increase in engine power", b = "The forward and backward movement of liquid in a partially filled tank",
            c = "A type of brake failure", d = "An electrical malfunction",
            correct = "B", explanation = "Surge is liquid movement inside a partly-filled tank that can push the truck forward, sideways, or affect braking.",
            tags = "tanker,surge",
        ),
        q(
            category = "Tank Vehicles", subCategory = "Baffles", difficulty = "Medium", isPremium = true,
            question = "What do baffles inside a tank do?",
            a = "Increase tank capacity", b = "Slow down the flow of surge inside the tank",
            c = "Clean the tank automatically", d = "Cool the liquid",
            correct = "B", explanation = "Baffles are internal walls with holes that slow liquid surge, though they don't stop side-to-side movement.",
            tags = "baffles",
        ),
    )

    private fun doublesTriples() = listOf(
        q(
            category = "Doubles/Triples", subCategory = "Coupling Order", difficulty = "Hard", isPremium = true,
            question = "When coupling a set of doubles, which trailer should you generally hook up first?",
            a = "The rearmost (second) trailer first", b = "The front trailer first, closest to the tractor",
            c = "It never matters", d = "Both simultaneously",
            correct = "B", explanation = "You typically couple the tractor to the front trailer first, then back that combination to the second trailer.",
            tags = "doubles,coupling order",
        ),
        q(
            category = "Doubles/Triples", subCategory = "Crack the Whip", difficulty = "Hard", isPremium = true,
            question = "The 'crack the whip' effect refers to:",
            a = "A braking technique", b = "Rearmost trailers amplifying a sudden movement made by the tractor",
            c = "A method for turning quickly", d = "A type of coupling device",
            correct = "B", explanation = "Sudden steering movements are amplified down the line of trailers, risking a rollover of the last trailer.",
            tags = "crack the whip",
        ),
    )

    private fun passengerVehicles() = listOf(
        q(
            category = "Passenger Vehicles", subCategory = "Loading", difficulty = "Medium", isPremium = true,
            question = "When loading passengers, the bus driver should:",
            a = "Rush passengers to save time", b = "Check mirrors and confirm the area is clear before moving",
            c = "Skip the headcount", d = "Allow standing beyond the marked line",
            correct = "B", explanation = "Safety checks before moving prevent injuries to boarding/alighting passengers and pedestrians nearby.",
            tags = "passenger,loading",
        ),
    )

    private fun stateSpecific() = listOf(
        q(
            state = "California", category = "State Specific", subCategory = "Speed Limits", difficulty = "Medium",
            question = "In California, the maximum speed limit for trucks with 3+ axles on most highways is:",
            a = "55 mph", b = "65 mph unless posted otherwise", c = "70 mph always", d = "No limit",
            correct = "A", explanation = "California sets a 55 mph limit for many heavy trucks unless a higher posted limit specifically applies.",
            tags = "california,speed limit",
        ),
        q(
            state = "Texas", category = "State Specific", subCategory = "Permit Requirements", difficulty = "Easy",
            question = "In Texas, what is the minimum age to apply for a CDL permit for intrastate driving?",
            a = "16", b = "18", c = "21", d = "25",
            correct = "B", explanation = "Texas allows intrastate CDL applicants at 18, while interstate commerce requires 21 per federal rule.",
            tags = "texas,age requirement",
        ),
        q(
            state = "Florida", category = "State Specific", subCategory = "Permit Requirements", difficulty = "Easy",
            question = "In Florida, how long is a commercial learner's permit (CLP) typically valid?",
            a = "30 days", b = "180 days, and may be renewed", c = "1 year with no renewal", d = "It never expires",
            correct = "B", explanation = "Florida's CLP is valid for 180 days and can generally be renewed for another 180 days.",
            tags = "florida,clp",
        ),
    )
}
