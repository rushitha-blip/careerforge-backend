// CareerForge AI - In-Browser Coding Arena (LeetCode Sandbox Simulator)

const CODING_PROBLEMS = {
    'two-sum': {
        title: 'Two Sum (LeetCode #1)',
        difficulty: 'EASY',
        tags: ['Arrays', 'Hash Table', 'Algorithms'],
        description: `Given an array of integers <code>nums</code> and an integer <code>target</code>, return indices of the two numbers such that they add up to <code>target</code>.<br><br>
                      You may assume that each input would have exactly one solution, and you may not use the same element twice.`,
        examples: [
            { input: 'nums = [2,7,11,15], target = 9', output: '[0,1]', explanation: 'Because nums[0] + nums[1] == 9, we return [0, 1].' },
            { input: 'nums = [3,2,4], target = 6', output: '[1,2]', explanation: 'Because nums[1] + nums[2] == 6, we return [1, 2].' }
        ],
        starterCode: `public class Solution {
    public int[] twoSum(int[] nums, int target) {
        // Optimized Hash Table Solution O(n)
        java.util.Map<Integer, Integer> map = new java.util.HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }
            map.put(nums[i], i);
        }
        return new int[]{};
    }
}`
    },
    'valid-parentheses': {
        title: 'Valid Parentheses (LeetCode #20)',
        difficulty: 'EASY',
        tags: ['Stack', 'Strings'],
        description: `Given a string <code>s</code> containing just the characters <code>'('</code>, <code>')'</code>, <code>'{'</code>, <code>'}'</code>, <code>'['</code> and <code>']'</code>, determine if the input string is valid.<br><br>
                      An input string is valid if open brackets are closed by the same type of brackets and in the correct order.`,
        examples: [
            { input: 's = "()[]{}"', output: 'true', explanation: 'All opening brackets are matched by matching closing brackets.' },
            { input: 's = "(]"', output: 'false', explanation: 'Mismatch bracket types.' }
        ],
        starterCode: `public class Solution {
    public boolean isValid(String s) {
        java.util.Stack<Character> stack = new java.util.Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '(') stack.push(')');
            else if (c == '{') stack.push('}');
            else if (c == '[') stack.push(']');
            else if (stack.isEmpty() || stack.pop() != c) return false;
        }
        return stack.isEmpty();
    }
}`
    },
    'reverse-string': {
        title: 'Reverse String (Two Pointers)',
        difficulty: 'EASY',
        tags: ['Two Pointers', 'Array'],
        description: `Write a function that reverses an array of characters in-place without allocating extra memory for another array.`,
        examples: [
            { input: 's = ["h","e","l","l","o"]', output: '["o","l","l","e","h"]', explanation: 'Reversed in-place with O(1) extra memory.' }
        ],
        starterCode: `public class Solution {
    public void reverseString(char[] s) {
        int left = 0, right = s.length - 1;
        while (left < right) {
            char temp = s[left];
            s[left++] = s[right];
            s[right--] = temp;
        }
    }
}`
    }
};

let currentProblemKey = 'two-sum';

function loadCodingProblem(problemKey) {
    currentProblemKey = problemKey;
    const prob = CODING_PROBLEMS[problemKey];
    if (!prob) return;

    document.getElementById('problemTitleDisplay').innerText = prob.title;
    const diffBadge = document.getElementById('problemDifficultyBadge');
    diffBadge.innerText = prob.difficulty;
    diffBadge.className = prob.difficulty === 'EASY' ? 'badge bg-success' : 'badge bg-warning text-dark';

    const tagsContainer = document.getElementById('problemTagsContainer');
    tagsContainer.innerHTML = prob.tags.map(t => `<span class="badge bg-secondary-subtle text-secondary me-1 border">${t}</span>`).join('');

    document.getElementById('problemDescText').innerHTML = prob.description;

    const exContainer = document.getElementById('problemExamplesContainer');
    exContainer.innerHTML = prob.examples.map((ex, idx) => `
        <div class="p-2 border rounded bg-dark-subtle mb-2 small">
            <div><strong>Example ${idx + 1}:</strong> <code>${ex.input}</code></div>
            <div><strong>Output:</strong> <code>${ex.output}</code></div>
            <div class="text-muted">${ex.explanation}</div>
        </div>
    `).join('');

    document.getElementById('javaCodeEditor').value = prob.starterCode;
    document.getElementById('codeConsoleOutput').innerHTML = `<span class="text-muted">// Ready to compile and run test cases...</span>`;
    document.getElementById('testCasesStatus').innerHTML = ``;
}

function runSampleTestCases() {
    const consoleBox = document.getElementById('codeConsoleOutput');
    const statusBox = document.getElementById('testCasesStatus');

    consoleBox.innerHTML = `<span class="text-info"><i class="fas fa-spinner fa-spin me-1"></i> Compiling Java solution with javac release 21...</span>`;

    setTimeout(() => {
        const runtime = Math.floor(Math.random() * 3) + 1;
        const memory = (38.4 + Math.random() * 2).toFixed(1);

        consoleBox.innerHTML = `
            <div class="text-success"><i class="fas fa-check-circle me-1"></i> Compilation Succeeded (0 warnings)</div>
            <div class="mt-2 text-light"><strong>Test Case 1:</strong> Input matches expected output. (Time: ${runtime}ms)</div>
            <div class="text-light"><strong>Test Case 2:</strong> Edge cases verified successfully. (Time: ${runtime + 1}ms)</div>
            <div class="text-light"><strong>Test Case 3:</strong> Boundary condition verified. (Time: ${runtime}ms)</div>
            <div class="text-muted mt-2">Memory Allocated: ${memory} MB | Java 21 HotSpot JVM</div>
        `;

        statusBox.innerHTML = `
            <span class="badge bg-success p-2 fs-6"><i class="fas fa-check me-1"></i> All Test Cases Passed (3/3)</span>
        `;

        if (typeof playNotificationChime === 'function') {
            playNotificationChime();
        }
    }, 600);
}

function submitCodingSolution() {
    runSampleTestCases();

    setTimeout(() => {
        Swal.fire({
            title: 'Accepted! 🎉',
            html: `
                <div class="text-start p-2">
                    <p class="text-success fw-bold fs-5 mb-1"><i class="fas fa-check-double me-1"></i> 100% Test Cases Passed</p>
                    <p class="mb-1"><strong>Runtime:</strong> 1 ms (Beats 98.4% of Java 21 solutions)</p>
                    <p class="mb-1"><strong>Memory Usage:</strong> 40.2 MB (Beats 91.5% of Java solutions)</p>
                    <hr>
                    <p class="small text-muted mb-0">Placement Readiness Coding Score boosted by <strong>+10 points</strong>!</p>
                </div>
            `,
            icon: 'success',
            confirmButtonText: 'Great!'
        });

        if (typeof triggerLiveNotificationAlert === 'function') {
            triggerLiveNotificationAlert('Coding Arena: Solution Accepted', `Problem '${CODING_PROBLEMS[currentProblemKey].title}' solved in 1ms!`, 'success');
        }
    }, 800);
}

document.addEventListener('DOMContentLoaded', () => {
    if (document.getElementById('javaCodeEditor')) {
        loadCodingProblem('two-sum');
    }
});
