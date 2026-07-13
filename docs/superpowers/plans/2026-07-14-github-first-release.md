# GitHub First Release Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a user-facing project README and publish the current Android project as the initial GitHub commit.

**Architecture:** Keep source code unchanged. Add one root-level Markdown document that describes the app and its install/build paths, then stage the existing project while relying on `.gitignore` to exclude local signing files and generated outputs.

**Tech Stack:** Markdown, Git, GitHub, Gradle.

## Global Constraints

- Repository: `https://github.com/xiaoxiaoxiaoHuanGe/usuallyAPP`.
- Do not stage `keystore.properties`, `*.jks`, `*.keystore`, `local.properties`, or generated build directories.
- The GitHub repository stores source code only; the signed APK is published separately as a Release asset.

---

### Task 1: Create the GitHub README

**Files:**
- Create: `README.md`
- Reference: `docs/superpowers/specs/2026-07-14-github-readme-design.md`

**Interfaces:**
- Consumes: confirmed README design specification.
- Produces: GitHub-rendered Chinese project introduction.

- [ ] **Step 1: Write the README**

Include the app introduction, core functions, local-data behavior, technical stack, Release installation, and Gradle build commands defined in the approved specification.

- [ ] **Step 2: Verify README content**

Run: `rg -n "猜车品牌|品牌专项|错题复盘|GitHub Releases|assembleRelease" README.md`

Expected: all five key topics are present.

### Task 2: Verify and publish the source repository

**Files:**
- Modify: Git index and branch metadata only.

**Interfaces:**
- Consumes: repository source files, `.gitignore`, and the GitHub remote URL.
- Produces: initial commit on remote `main` branch.

- [ ] **Step 1: Stage project files**

Run: `git add -A`

- [ ] **Step 2: Verify secrets and generated outputs are excluded**

Run: `git check-ignore -v keystore.properties local.properties app/build/outputs/apk/release/app-release.apk`

Expected: each path matches an ignore rule.

- [ ] **Step 3: Create the initial commit**

Run: `git commit -m "feat: initial Guess Car Brand release"`

- [ ] **Step 4: Set remote and push main**

Run: `git branch -M main`; `git remote add origin https://github.com/xiaoxiaoxiaoHuanGe/usuallyAPP.git`; `git push -u origin main`

Expected: GitHub receives the `main` branch.
