# Design

## Context

决策包页签（`activeStep === 2`）复用 `el-card.guided-workflow` 外壳，内部是 `section.guidance-result-panel.packet-result-panel`（业务决策包）再包一层子卡片，形成三层卡片视觉。`--viewport` 视口滚动类当前仅 `activeStep < 2` 生效，因此第三步整页滚动、右侧聊天框随内容滚走。动机见 proposal.md。

## Goals / Non-Goals

**Goals:**
- 修订标签紧贴“业务决策包”标题右侧并垂直居中。
- 去掉“业务决策包”层独立边框/背景/padding，子卡片直接平铺在外壳卡片内。
- 决策包步骤左侧内容在左侧卡片内部纵向滚动，聊天框保持可见。

**Non-Goals:**
- 不改变决策包数据、生成逻辑或流程文件功能。
- 不改动业务说明、模型协作步骤的布局与滚动行为。

## Decisions

1. **视口滚动扩展到决策包步骤**
   - 将模板 `--viewport` 条件由 `activeStep < 2` 改为 `activeStep !== 0`。
   - 理由：业务说明步骤已隐藏聊天框并占满整行，无需视口滚动；模型协作与决策包步骤都保留右侧聊天框，需要左侧内部滚动保持聊天框可见。复用既有 `--viewport` 的 `el-card__body` 内部滚动机制。
   - 备选：为决策包单独写一套滚动样式——重复且易与既有机制漂移，不采用。

2. **扁平化“业务决策包”层**
   - 用 `.guidance-result-panel.packet-result-panel` 双类选择器覆盖 `padding/border/background/margin-top`，使 `section.packet-result-panel` 直接作为外壳卡片内容。
   - 理由：`packet-result-panel` 与第二步“检查结论”面板共用 `.guidance-result-panel` 基础样式，双类选择器只命中决策包外层，避免影响流程文件子卡片（它仍是 `.guidance-result-panel`，保留卡片外观）与第二步面板。
   - 备选：直接改 `.guidance-result-panel` 基础样式——会波及第二步与流程文件卡片，不采用。

3. **修订标签移到标题旁**
   - 用 `packet-title-row`（flex + 居中）包裹标题与修订标签，作为 `card-heading` 的第一个子项，“返回模型协作”保持右侧。
   - 理由：`card-heading` 为 `space-between`，单独把标签放中间会产生不贴标题的间隔；分组后标签紧贴标题且垂直居中。

## Risks / Trade-offs

- [第三步加入视口滚动后，窄内容也可能出现空滚动条] → 与模型协作步骤一致，可接受。
- [扁平化后“业务决策包”标题与子卡片间距需手动维持] → 保留 `card-heading` 的 `margin-bottom: 16px`，不额外引入间距系统。
