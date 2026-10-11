<template>
  <section class="evidence-page list-page entry-page ems-business-knowledge-page">
    <div class="page-heading entry-page-heading">
      <h2>EMS业务知识域</h2>
    </div>

    <el-alert
      title="本目录为本地简易版的结构快照，不读取 EMS 实时数据；跨表关系、状态枚举和权威来源仍需按场景补充样本或代码证据。"
      type="info"
      :closable="false"
      show-icon
    />

    <el-alert v-if="errorMessage" class="knowledge-alert" type="error" :closable="false" show-icon>
      <div slot="title" class="knowledge-error-title">
        <span>{{ errorMessage }}</span>
        <el-button type="text" @click="loadDomains">重新加载</el-button>
      </div>
    </el-alert>

    <section v-else class="entry-content-card knowledge-card">
      <div class="entry-content-toolbar">
        <span class="entry-content-toolbar__meta">已收录 {{ domains.length }} 个业务域</span>
        <el-button type="text" :loading="loading" @click="loadDomains">刷新目录</el-button>
      </div>

      <div v-if="!loading && domains.length === 0" class="knowledge-empty">暂无可展示的 EMS 业务知识域</div>

      <el-collapse v-else v-model="openedDomains" class="knowledge-domain-list">
        <el-collapse-item v-for="domain in domains" :key="domain.code" :name="domain.code">
          <template slot="title">
            <div class="knowledge-domain-title">
              <strong>{{ domain.name }}</strong>
              <span>{{ domain.schemas.join('、') }} · {{ domain.tableCount }} 张结构快照表</span>
            </div>
          </template>
          <div class="knowledge-domain-content">
            <p>{{ domain.summary }}</p>
            <div class="knowledge-field">
              <span>关键表</span>
              <div>
                <el-tag v-for="table in domain.keyTables" :key="table" effect="plain" class="knowledge-table-tag">
                  {{ table }}
                </el-tag>
              </div>
            </div>
            <div class="knowledge-field">
              <span>可信边界</span>
              <p>{{ domain.knowledgeBoundary }}</p>
            </div>
            <div class="knowledge-field">
              <span>后续核验</span>
              <p>{{ domain.verificationHint }}</p>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </section>
  </section>
</template>

<script lang="ts">
import Vue from 'vue'

import { EmsBusinessDomainKnowledge, listEmsBusinessDomainKnowledge } from '../services/ems-business-knowledge-api'

/** 展示受控、只读的 EMS 业务域结构知识。 */
export default Vue.extend({
  name: 'EmsBusinessKnowledge',
  data() {
    return {
      domains: [] as EmsBusinessDomainKnowledge[],
      openedDomains: [] as string[],
      loading: false,
      errorMessage: '',
    }
  },
  created() {
    void this.loadDomains()
  },
  methods: {
    /** 重新加载目录；失败时不保留可能已过期的展示内容。 */
    async loadDomains(): Promise<void> {
      this.loading = true
      this.errorMessage = ''
      try {
        const domains = await listEmsBusinessDomainKnowledge()
        this.domains = domains
        this.openedDomains = domains.length > 0 ? [domains[0].code] : []
      } catch (error: unknown) {
        this.domains = []
        this.openedDomains = []
        this.errorMessage = error instanceof Error ? error.message : 'EMS业务知识域暂时无法读取'
      } finally {
        this.loading = false
      }
    },
  },
})
</script>

<style scoped>
.knowledge-alert {
  margin-top: 16px;
}

.knowledge-error-title {
  display: flex;
  gap: 12px;
  align-items: center;
}

.knowledge-card {
  margin-top: 16px;
}

.knowledge-empty {
  padding: 42px 20px;
  color: #6b778c;
  text-align: center;
}

.knowledge-domain-list {
  border-top: 1px solid #e1e7ef;
}

.knowledge-domain-title {
  display: flex;
  gap: 12px;
  align-items: center;
  min-width: 0;
}

.knowledge-domain-title strong {
  color: #23314d;
}

.knowledge-domain-title span {
  overflow: hidden;
  color: #6b778c;
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.knowledge-domain-content {
  padding: 4px 8px 18px;
  color: #40506d;
  line-height: 1.75;
}

.knowledge-domain-content > p {
  margin: 0 0 14px;
}

.knowledge-field {
  display: grid;
  grid-template-columns: 76px minmax(0, 1fr);
  gap: 12px;
  margin-top: 10px;
}

.knowledge-field > span {
  color: #697891;
  font-weight: 600;
}

.knowledge-field p {
  margin: 0;
}

.knowledge-table-tag {
  margin: 0 8px 8px 0;
  color: #4669a6;
}

@media (max-width: 768px) {
  .knowledge-domain-title {
    display: block;
  }

  .knowledge-domain-title span {
    display: block;
  }

  .knowledge-field {
    display: block;
  }

  .knowledge-field > span {
    display: block;
    margin-bottom: 4px;
  }
}
</style>
