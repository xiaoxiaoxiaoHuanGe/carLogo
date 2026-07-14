# Self-Contained Handoff Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Create a root-level Markdown handoff that lets a new task conversation maintain and release the app without relying on chat context.

**Architecture:** Document verified project facts in a single root `handoff.md`; link from the document to source entrypoints, current GitHub artifacts, and existing project docs. Do not alter application code or IDE metadata.

**Tech Stack:** Markdown, Git, Android Gradle Plugin, Kotlin, Jetpack Compose, Room.

## Global Constraints

- Create only `handoff.md` and this implementation-plan document.
- Preserve all pre-existing `.idea` working-tree modifications and untracked files.
- Never write secrets, keystore passwords, or token values into the handoff.
- Clearly distinguish verified current facts from recommended next work.

---

### Task 1: Write the self-contained handoff

**Files:**
- Create: `handoff.md`
- Reference: `docs/superpowers/specs/2026-07-14-self-contained-handoff-design.md`

**Interfaces:**
- Consumes: project Gradle configuration, source layout, tests, Git commit `9c1ebed`, and published Release `v1.0.0`.
- Produces: a context-independent handoff document for a future maintenance conversation.

- [ ] **Step 1: Record project identity and verified state**

State the app purpose, package `com.example.carlogo`, current repository, Release URL, release asset name, and the latest commit hash.

- [ ] **Step 2: Record functionality, open work, technology and resources**

Include at least five completed items, at least three prioritized time-estimated tasks, exact dependency versions, build requirements, key paths, and access boundaries.

- [ ] **Step 3: Record risks and a new-conversation prompt**

Describe signing, local-data, versioning, source-of-truth, and IDE-file risks. End with a copyable prompt that directs a new task to read this document first.

### Task 2: Verify handoff completeness

**Files:**
- Test: `handoff.md`

**Interfaces:**
- Consumes: the generated handoff Markdown.
- Produces: an explicit requirement-coverage check.

- [ ] **Step 1: Check mandatory sections**

Run: `rg -n "项目概述|已完成的工作|待办事项|技术栈与依赖|文档与资源|风险与注意事项|新任务对话" handoff.md`

Expected: all seven headings are present.

- [ ] **Step 2: Check minimum-item requirements**

Run: inspect the completed-work and todo lists in `handoff.md`.

Expected: at least five completed items and at least three todos, each todo including priority and an hour estimate.
