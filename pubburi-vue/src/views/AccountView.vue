<template>
  <section class="view account-layout">
    <div v-if="!auth.user" class="auth-panel">
      <div class="segmented">
        <button type="button" :class="{ active: auth.authMode === 'login' }" @click="auth.authMode = 'login'">로그인</button>
        <button type="button" :class="{ active: auth.authMode === 'join' }" @click="auth.authMode = 'join'">회원가입</button>
      </div>
      <form v-if="auth.authMode === 'login'" @submit.prevent="login">
        <input v-model="loginForm.id" class="form-control" placeholder="아이디" autocomplete="username" required />
        <input v-model="loginForm.pass" type="password" class="form-control" placeholder="비밀번호" autocomplete="current-password" required />
        <button type="submit" class="btn btn-dark w-100">로그인</button>
      </form>
      <form v-else @submit.prevent="join">
        <input v-model="joinForm.id" class="form-control" placeholder="아이디" required />
        <input v-model="joinForm.name" class="form-control" placeholder="이름" required />
        <input v-model="joinForm.pass" type="password" class="form-control" placeholder="비밀번호" required />
        <button type="submit" class="btn btn-dark w-100">가입</button>
      </form>
    </div>
    <ProfileView v-else />
  </section>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import ProfileView from './ProfileView.vue'
import { useAuthStore } from '../stores/auth'
import { useUiStore } from '../stores/ui'

const auth = useAuthStore()
const ui = useUiStore()
const router = useRouter()
const loginForm = reactive({ id: '', pass: '' })
const joinForm = reactive({ id: '', name: '', pass: '' })

async function login() {
  await ui.run(async () => {
    await auth.login(loginForm)
    loginForm.pass = ''
    await router.push('/profile')
  }, '로그인되었습니다.')
}

async function join() {
  await ui.run(async () => {
    await auth.join(joinForm)
    await auth.login({ id: joinForm.id, pass: joinForm.pass })
    joinForm.id = ''
    joinForm.name = ''
    joinForm.pass = ''
    await router.push('/profile')
  }, '가입되었습니다.')
}
</script>
