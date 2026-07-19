import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { AccountService } from 'app/core/auth/account.service';
import LanguageSwitcher from '../language-switcher/language-switcher';
import { TranslateModule } from '@ngx-translate/core';
import { TranslateDirective } from 'app/shared/language';
import { LoginService } from 'app/login/login.service';
import HasAnyAuthorityDirective from 'app/shared/auth/has-any-authority.directive';

@Component({
  selector: 'jhi-sidebar',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    RouterLink,
    RouterLinkActive,
    FontAwesomeModule,
    LanguageSwitcher,
    TranslateModule,
    TranslateDirective,
    HasAnyAuthorityDirective,
  ],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss',
})
export default class Sidebar implements OnInit {
  private readonly loginService = inject(LoginService);
  readonly account = inject(AccountService).account;
  private readonly router = inject(Router);

  ngOnInit(): void {}

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.loginService.logout();
    this.router.navigate(['']);
  }
}
