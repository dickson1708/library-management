import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILibraryMember, NewLibraryMember } from '../library-member.model';

export type PartialUpdateLibraryMember = Partial<ILibraryMember> & Pick<ILibraryMember, 'id'>;

type RestOf<T extends ILibraryMember | NewLibraryMember> = Omit<T, 'birthDate' | 'registrationDate'> & {
  birthDate?: string | null;
  registrationDate?: string | null;
};

export type RestLibraryMember = RestOf<ILibraryMember>;

export type NewRestLibraryMember = RestOf<NewLibraryMember>;

export type PartialUpdateRestLibraryMember = RestOf<PartialUpdateLibraryMember>;

@Injectable()
export class LibraryMembersService {
  readonly libraryMembersParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly libraryMembersResource = httpResource<RestLibraryMember[]>(() => {
    const params = this.libraryMembersParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of libraryMember that have been fetched. It is updated when the libraryMembersResource emits a new value.
   * In case of error while fetching the libraryMembers, the signal is set to an empty array.
   */
  readonly libraryMembers = computed(() =>
    (this.libraryMembersResource.hasValue() ? this.libraryMembersResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/library-members');

  protected convertValueFromServer(restLibraryMember: RestLibraryMember): ILibraryMember {
    return {
      ...restLibraryMember,
      birthDate: restLibraryMember.birthDate ? dayjs(restLibraryMember.birthDate) : undefined,
      registrationDate: restLibraryMember.registrationDate ? dayjs(restLibraryMember.registrationDate) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class LibraryMemberService extends LibraryMembersService {
  protected readonly http = inject(HttpClient);

  create(libraryMember: NewLibraryMember): Observable<ILibraryMember> {
    const copy = this.convertValueFromClient(libraryMember);
    return this.http.post<RestLibraryMember>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(libraryMember: ILibraryMember): Observable<ILibraryMember> {
    const copy = this.convertValueFromClient(libraryMember);
    return this.http
      .put<RestLibraryMember>(`${this.resourceUrl}/${encodeURIComponent(this.getLibraryMemberIdentifier(libraryMember))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(libraryMember: PartialUpdateLibraryMember): Observable<ILibraryMember> {
    const copy = this.convertValueFromClient(libraryMember);
    return this.http
      .patch<RestLibraryMember>(`${this.resourceUrl}/${encodeURIComponent(this.getLibraryMemberIdentifier(libraryMember))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ILibraryMember> {
    return this.http
      .get<RestLibraryMember>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ILibraryMember[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestLibraryMember[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLibraryMemberIdentifier(libraryMember: Pick<ILibraryMember, 'id'>): number {
    return libraryMember.id;
  }

  compareLibraryMember(o1: Pick<ILibraryMember, 'id'> | null, o2: Pick<ILibraryMember, 'id'> | null): boolean {
    return o1 && o2 ? this.getLibraryMemberIdentifier(o1) === this.getLibraryMemberIdentifier(o2) : o1 === o2;
  }

  addLibraryMemberToCollectionIfMissing<Type extends Pick<ILibraryMember, 'id'>>(
    libraryMemberCollection: Type[],
    ...libraryMembersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const libraryMembers: Type[] = libraryMembersToCheck.filter(isPresent);
    if (libraryMembers.length > 0) {
      const libraryMemberCollectionIdentifiers = libraryMemberCollection.map(libraryMemberItem =>
        this.getLibraryMemberIdentifier(libraryMemberItem),
      );
      const libraryMembersToAdd = libraryMembers.filter(libraryMemberItem => {
        const libraryMemberIdentifier = this.getLibraryMemberIdentifier(libraryMemberItem);
        if (libraryMemberCollectionIdentifiers.includes(libraryMemberIdentifier)) {
          return false;
        }
        libraryMemberCollectionIdentifiers.push(libraryMemberIdentifier);
        return true;
      });
      return [...libraryMembersToAdd, ...libraryMemberCollection];
    }
    return libraryMemberCollection;
  }

  protected convertValueFromClient<T extends ILibraryMember | NewLibraryMember | PartialUpdateLibraryMember>(libraryMember: T): RestOf<T> {
    return {
      ...libraryMember,
      birthDate: libraryMember.birthDate?.format(DATE_FORMAT) ?? null,
      registrationDate: libraryMember.registrationDate?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestLibraryMember): ILibraryMember {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestLibraryMember[]): ILibraryMember[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
